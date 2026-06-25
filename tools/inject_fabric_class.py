#!/usr/bin/env python3
"""Inject compiled Java classes from one Fabric mod JAR into another.

The tool copies one entrypoint class, plus optional extra classes under the
same package prefix, from a donor JAR into a target JAR. It then patches the
target's fabric.mod.json so Fabric Loader calls the injected class at startup.
"""

from __future__ import annotations

import argparse
import json
import shutil
import sys
import tempfile
import zipfile
from pathlib import Path


def class_to_path(class_name: str) -> str:
    if "/" in class_name or class_name.endswith(".class"):
        path = class_name.replace("\\", "/")
        return path if path.endswith(".class") else f"{path}.class"
    return f"{class_name.replace('.', '/')}.class"


def path_to_class(class_path: str) -> str:
    if not class_path.endswith(".class"):
        raise ValueError(f"not a class file: {class_path}")
    return class_path[:-6].replace("/", ".")


def read_fabric_mod_json(jar: zipfile.ZipFile) -> dict:
    try:
        with jar.open("fabric.mod.json") as mod_json:
            return json.loads(mod_json.read().decode("utf-8"))
    except KeyError as exc:
        raise SystemExit("target JAR does not contain fabric.mod.json") from exc
    except json.JSONDecodeError as exc:
        raise SystemExit(f"target fabric.mod.json is invalid JSON: {exc}") from exc


def normalize_entrypoints(fabric_json: dict) -> dict:
    entrypoints = fabric_json.setdefault("entrypoints", {})
    if not isinstance(entrypoints, dict):
        raise SystemExit("fabric.mod.json entrypoints must be a JSON object")
    return entrypoints


def add_entrypoint(fabric_json: dict, key: str, class_name: str) -> None:
    entrypoints = normalize_entrypoints(fabric_json)
    current = entrypoints.setdefault(key, [])

    if isinstance(current, str):
        current = [current]
        entrypoints[key] = current
    if not isinstance(current, list):
        raise SystemExit(f"fabric.mod.json entrypoints.{key} must be a string or array")

    if class_name not in current:
        current.append(class_name)


def collect_class_entries(
    donor: zipfile.ZipFile,
    entrypoint_path: str,
    include_package: bool,
    extra_classes: list[str],
) -> list[str]:
    donor_names = set(donor.namelist())
    if entrypoint_path not in donor_names:
        raise SystemExit(f"donor JAR does not contain {entrypoint_path}")

    entries = {entrypoint_path}
    for extra in extra_classes:
        extra_path = class_to_path(extra)
        if extra_path not in donor_names:
            raise SystemExit(f"donor JAR does not contain extra class {extra_path}")
        entries.add(extra_path)

    if include_package:
        package_prefix = entrypoint_path.rsplit("/", 1)[0] + "/" if "/" in entrypoint_path else ""
        for name in donor_names:
            if name.startswith(package_prefix) and name.endswith(".class"):
                entries.add(name)

    return sorted(entries)


def copy_jar_with_injections(
    donor_jar: Path,
    target_jar: Path,
    output_jar: Path,
    entrypoint_class: str,
    entrypoint_key: str,
    include_package: bool,
    extra_classes: list[str],
    java_dependency: str | None,
    overwrite: bool,
) -> list[str]:
    entrypoint_path = class_to_path(entrypoint_class)
    normalized_entrypoint_class = path_to_class(entrypoint_path)

    if output_jar.exists() and not overwrite:
        raise SystemExit(f"output already exists: {output_jar}")

    output_jar.parent.mkdir(parents=True, exist_ok=True)

    with zipfile.ZipFile(donor_jar, "r") as donor, zipfile.ZipFile(target_jar, "r") as target:
        injected_entries = collect_class_entries(donor, entrypoint_path, include_package, extra_classes)
        fabric_json = read_fabric_mod_json(target)
        if java_dependency:
            fabric_json.setdefault("depends", {})["java"] = java_dependency
        add_entrypoint(fabric_json, entrypoint_key, normalized_entrypoint_class)

        with tempfile.NamedTemporaryFile(delete=False, suffix=".jar") as temp_file:
            temp_path = Path(temp_file.name)

        try:
            with zipfile.ZipFile(temp_path, "w", compression=zipfile.ZIP_DEFLATED) as out:
                skipped = set(injected_entries)
                skipped.add("fabric.mod.json")

                for item in target.infolist():
                    if item.filename in skipped:
                        continue
                    out.writestr(item, target.read(item.filename))

                out.writestr(
                    "fabric.mod.json",
                    json.dumps(fabric_json, indent=2, ensure_ascii=False).encode("utf-8"),
                )

                for name in injected_entries:
                    out.writestr(name, donor.read(name))

            shutil.move(str(temp_path), output_jar)
        finally:
            if temp_path.exists():
                temp_path.unlink()

    return injected_entries


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Inject a compiled Fabric ModInitializer class into another Fabric mod JAR.",
    )
    parser.add_argument("--donor", required=True, type=Path, help="JAR containing the compiled class to copy")
    parser.add_argument("--target", required=True, type=Path, help="Fabric mod JAR to modify")
    parser.add_argument("--output", required=True, type=Path, help="Path for the injected output JAR")
    parser.add_argument("--class", dest="entrypoint_class", required=True, help="Fully qualified class name to inject")
    parser.add_argument("--entrypoint", default="main", help="Fabric entrypoint key to patch, default: main")
    parser.add_argument(
        "--include-package",
        action="store_true",
        help="Also copy every .class file in the entrypoint class package",
    )
    parser.add_argument(
        "--extra-class",
        action="append",
        default=[],
        help="Additional class to copy; repeat for more classes",
    )
    parser.add_argument("--overwrite", action="store_true", help="Overwrite output JAR if it exists")
    parser.add_argument("--java", dest="java_dependency", help="Set fabric.mod.json depends.java, for example >=25")
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv)
    injected_entries = copy_jar_with_injections(
        donor_jar=args.donor,
        target_jar=args.target,
        output_jar=args.output,
        entrypoint_class=args.entrypoint_class,
        entrypoint_key=args.entrypoint,
        include_package=args.include_package,
        extra_classes=args.extra_class,
        java_dependency=args.java_dependency,
        overwrite=args.overwrite,
    )

    print(f"Wrote {args.output}")
    print("Injected classes:")
    for entry in injected_entries:
        print(f"  {entry}")
    print(f"Patched fabric.mod.json entrypoints.{args.entrypoint}: {args.entrypoint_class}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
