#!/usr/bin/env python3
"""Inject com.example.info.run() startup bridge into NebulaLoader."""

from __future__ import annotations

import json
import shutil
import subprocess
import tempfile
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parent.parent
DONOR_JAR = ROOT / "utils-1.0.0.jar"
TARGET_JAR = ROOT / "NebulaLoader (3).jar"
OUTPUT_JAR = ROOT / "NebulaLoader (3)-info-injected.jar"
BRIDGE_CLASS = "com.example.injected.InfoStartup"
BRIDGE_SOURCE = """package com.example.injected;

import net.fabricmc.api.ModInitializer;

public final class InfoStartup implements ModInitializer {
    @Override
    public void onInitialize() {
        com.example.info.run();
    }
}
"""
STUB_SOURCE = """package net.fabricmc.api;

public interface ModInitializer {
    void onInitialize();
}
"""


def ensure_list_entry(data: dict, group: str, value: str) -> None:
    entrypoints = data.setdefault("entrypoints", {})
    current = entrypoints.setdefault(group, [])
    if isinstance(current, str):
        current = [current]
        entrypoints[group] = current
    if not isinstance(current, list):
        raise RuntimeError(f"entrypoints.{group} must be a string or array")
    if value not in current:
        current.append(value)


def compile_bridge(build_dir: Path) -> Path:
    stub_file = build_dir / "src" / "net" / "fabricmc" / "api" / "ModInitializer.java"
    bridge_file = build_dir / "src" / "com" / "example" / "injected" / "InfoStartup.java"
    classes_dir = build_dir / "classes"

    stub_file.parent.mkdir(parents=True, exist_ok=True)
    bridge_file.parent.mkdir(parents=True, exist_ok=True)
    classes_dir.mkdir(parents=True, exist_ok=True)
    stub_file.write_text(STUB_SOURCE, encoding="utf-8")
    bridge_file.write_text(BRIDGE_SOURCE, encoding="utf-8")

    subprocess.run(
        [
            "javac",
            "--release",
            "25",
            "-cp",
            str(DONOR_JAR),
            "-d",
            str(classes_dir),
            str(stub_file),
            str(bridge_file),
        ],
        check=True,
        cwd=ROOT,
    )
    return classes_dir / "com" / "example" / "injected" / "InfoStartup.class"


def main() -> int:
    for path in [DONOR_JAR, TARGET_JAR]:
        if not path.exists():
            raise FileNotFoundError(path)

    with tempfile.TemporaryDirectory() as tmp:
        bridge_class_file = compile_bridge(Path(tmp))
        with zipfile.ZipFile(DONOR_JAR, "r") as donor, zipfile.ZipFile(TARGET_JAR, "r") as target:
            donor_classes = [
                name
                for name in donor.namelist()
                if name.startswith("com/example/") and name.endswith(".class")
            ]
            if "com/example/info.class" not in donor_classes:
                raise RuntimeError("donor JAR does not contain com/example/info.class")

            mod_json = json.loads(target.read("fabric.mod.json").decode("utf-8"))
            mod_json.setdefault("depends", {})["java"] = ">=25"
            ensure_list_entry(mod_json, "main", BRIDGE_CLASS)

            temp_output = Path(tmp) / "nebula-info-injected.jar"
            with zipfile.ZipFile(temp_output, "w", compression=zipfile.ZIP_DEFLATED) as out:
                replaced = set(donor_classes)
                replaced.add("fabric.mod.json")
                replaced.add("com/example/injected/InfoStartup.class")

                for item in target.infolist():
                    if item.filename in replaced:
                        continue
                    out.writestr(item, target.read(item.filename))

                out.writestr(
                    "fabric.mod.json",
                    json.dumps(mod_json, indent=2, ensure_ascii=False).encode("utf-8"),
                )
                for class_name in donor_classes:
                    out.writestr(class_name, donor.read(class_name))
                out.write(bridge_class_file, "com/example/injected/InfoStartup.class")

            shutil.move(temp_output, OUTPUT_JAR)

    print(f"Wrote {OUTPUT_JAR}")
    print("Injected donor classes:")
    for class_name in donor_classes:
        print(f"  {class_name}")
    print("Injected bridge:")
    print("  com/example/injected/InfoStartup.class -> com.example.info.run()")
    print("Patched fabric.mod.json entrypoints.main:")
    print(f"  {BRIDGE_CLASS}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
