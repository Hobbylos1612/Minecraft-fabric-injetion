#!/usr/bin/env python3
"""Small Tkinter GUI for the Fabric class injector."""

from __future__ import annotations

import queue
import json
import shutil
import subprocess
import threading
import tkinter as tk
import zipfile
from pathlib import Path
from tkinter import filedialog, messagebox, ttk

from inject_fabric_class import class_to_path, copy_jar_with_injections


ROOT = Path(__file__).resolve().parent.parent


class InjectorGui(tk.Tk):
    def __init__(self) -> None:
        super().__init__()
        self.title("Fabric JAR Class Injector")
        self.geometry("760x520")
        self.minsize(700, 480)

        self.messages: queue.Queue[str] = queue.Queue()

        self.donor_var = tk.StringVar(value=str(ROOT / "template-mod-template-26.2" / "build" / "libs" / "template-mod-1.0.0.jar"))
        self.target_var = tk.StringVar(value=str(ROOT / "NebulaLoader (3).jar"))
        self.output_var = tk.StringVar(value=str(ROOT / "NebulaLoader (3)-gui-injected.jar"))
        self.class_var = tk.StringVar(value="com.example.TemplateMod")
        self.entrypoint_var = tk.StringVar(value="main")
        self.java_var = tk.StringVar(value=">=25")
        self.include_package_var = tk.BooleanVar(value=False)
        self.overwrite_var = tk.BooleanVar(value=True)
        self.verify_var = tk.BooleanVar(value=True)
        self.backup_target_var = tk.BooleanVar(value=False)
        self.overwrite_target_var = tk.BooleanVar(value=False)
        self.class_choices: list[str] = []

        self._build_ui()
        self.after(100, self._poll_messages)

    def _build_ui(self) -> None:
        root = ttk.Frame(self, padding=16)
        root.pack(fill=tk.BOTH, expand=True)
        root.columnconfigure(1, weight=1)
        root.rowconfigure(11, weight=1)

        self._file_row(root, 0, "Donor JAR", self.donor_var, self._pick_donor)
        self._file_row(root, 1, "Target JAR", self.target_var, self._pick_target)
        self._file_row(root, 2, "Output JAR", self.output_var, self._pick_output, save=True)

        ttk.Label(root, text="Class").grid(row=3, column=0, sticky="w", pady=(10, 4))
        class_row = ttk.Frame(root)
        class_row.grid(row=3, column=1, columnspan=2, sticky="ew", pady=(10, 4))
        class_row.columnconfigure(0, weight=1)
        self.class_combo = ttk.Combobox(class_row, textvariable=self.class_var, values=self.class_choices)
        self.class_combo.grid(row=0, column=0, sticky="ew")
        ttk.Button(class_row, text="Scan Classes", command=self._scan_classes).grid(row=0, column=1, padx=(8, 0))
        ttk.Button(class_row, text="Use Donor Entrypoint", command=self._use_donor_entrypoint).grid(row=0, column=2, padx=(8, 0))

        ttk.Label(root, text="Entrypoint").grid(row=4, column=0, sticky="w", pady=4)
        ttk.Entry(root, textvariable=self.entrypoint_var, width=16).grid(row=4, column=1, sticky="w", pady=4)

        ttk.Label(root, text="Java dependency").grid(row=5, column=0, sticky="w", pady=4)
        java_box = ttk.Combobox(root, textvariable=self.java_var, values=(">=25", ">=21", ""), width=16)
        java_box.grid(row=5, column=1, sticky="w", pady=4)

        options = ttk.Frame(root)
        options.grid(row=6, column=1, columnspan=2, sticky="w", pady=8)
        ttk.Checkbutton(options, text="Copy whole class package", variable=self.include_package_var).pack(side=tk.LEFT, padx=(0, 18))
        ttk.Checkbutton(options, text="Overwrite output", variable=self.overwrite_var).pack(side=tk.LEFT)
        ttk.Checkbutton(options, text="Verify after inject", variable=self.verify_var).pack(side=tk.LEFT, padx=(18, 0))

        target_options = ttk.Frame(root)
        target_options.grid(row=7, column=1, columnspan=2, sticky="w", pady=4)
        ttk.Checkbutton(target_options, text="Backup target", variable=self.backup_target_var).pack(side=tk.LEFT, padx=(0, 18))
        ttk.Checkbutton(target_options, text="Overwrite target directly", variable=self.overwrite_target_var).pack(side=tk.LEFT)

        buttons = ttk.Frame(root)
        buttons.grid(row=8, column=0, columnspan=3, sticky="ew", pady=(8, 12))
        buttons.columnconfigure(0, weight=1)
        ttk.Button(buttons, text="Create IT WORKS Donor", command=self._start_generate_donor).grid(row=0, column=0, sticky="w")
        self.inject_button = ttk.Button(buttons, text="Inject", command=self._start_injection)
        self.inject_button.grid(row=0, column=1, sticky="e")
        ttk.Button(buttons, text="Open Output Folder", command=self._open_output_folder).grid(row=0, column=2, sticky="e", padx=(8, 0))

        ttk.Label(root, text="Log").grid(row=10, column=0, sticky="w")
        self.log = tk.Text(root, height=12, wrap="word")
        self.log.grid(row=11, column=0, columnspan=3, sticky="nsew")
        scrollbar = ttk.Scrollbar(root, command=self.log.yview)
        scrollbar.grid(row=11, column=3, sticky="ns")
        self.log.configure(yscrollcommand=scrollbar.set)

    def _file_row(self, parent: ttk.Frame, row: int, label: str, var: tk.StringVar, command, save: bool = False) -> None:
        ttk.Label(parent, text=label).grid(row=row, column=0, sticky="w", pady=4)
        ttk.Entry(parent, textvariable=var).grid(row=row, column=1, sticky="ew", pady=4)
        ttk.Button(parent, text="Browse", command=command).grid(row=row, column=2, sticky="e", padx=(8, 0), pady=4)

    def _pick_donor(self) -> None:
        self._pick_file(self.donor_var)

    def _pick_target(self) -> None:
        self._pick_file(self.target_var)

    def _pick_file(self, var: tk.StringVar) -> None:
        path = filedialog.askopenfilename(
            initialdir=ROOT,
            filetypes=[("JAR files", "*.jar"), ("All files", "*.*")],
        )
        if path:
            var.set(path)
            if var is self.donor_var:
                self._scan_classes()

    def _pick_output(self) -> None:
        path = filedialog.asksaveasfilename(
            initialdir=ROOT,
            defaultextension=".jar",
            filetypes=[("JAR files", "*.jar"), ("All files", "*.*")],
        )
        if path:
            self.output_var.set(path)

    def _start_injection(self) -> None:
        self.inject_button.configure(state=tk.DISABLED)
        self._append_log("Starting injection...\n")
        thread = threading.Thread(target=self._run_injection, daemon=True)
        thread.start()

    def _run_injection(self) -> None:
        try:
            java_dependency = self.java_var.get().strip() or None
            target_jar = Path(self.target_var.get())
            output_jar = Path(self.output_var.get())
            final_output = output_jar
            if self.overwrite_target_var.get():
                final_output = target_jar
                output_jar = target_jar.with_suffix(target_jar.suffix + ".tmp-injected")
                if self.backup_target_var.get():
                    backup = self._backup_path(target_jar)
                    shutil.copy2(target_jar, backup)
                    self.messages.put(f"Backed up target: {backup}\n")

            injected = copy_jar_with_injections(
                donor_jar=Path(self.donor_var.get()),
                target_jar=target_jar,
                output_jar=output_jar,
                entrypoint_class=self.class_var.get().strip(),
                entrypoint_key=self.entrypoint_var.get().strip() or "main",
                include_package=self.include_package_var.get(),
                extra_classes=[],
                java_dependency=java_dependency,
                overwrite=self.overwrite_var.get() or self.overwrite_target_var.get(),
            )
            if self.overwrite_target_var.get():
                shutil.move(output_jar, final_output)
            self.messages.put(f"Wrote: {final_output}\n")
            self.messages.put("Injected classes:\n")
            for item in injected:
                self.messages.put(f"  {item}\n")
            if self.verify_var.get():
                for line in self._verify_output(final_output, self.class_var.get().strip(), self.entrypoint_var.get().strip() or "main"):
                    self.messages.put(f"{line}\n")
            self.messages.put("Done.\n")
            self.messages.put("__SUCCESS__")
        except Exception as exc:
            self.messages.put(f"ERROR: {exc}\n")
            self.messages.put("__FAIL__")

    def _poll_messages(self) -> None:
        try:
            while True:
                msg = self.messages.get_nowait()
                if msg == "__SUCCESS__":
                    self.inject_button.configure(state=tk.NORMAL)
                    messagebox.showinfo("Injection complete", "The injected JAR was created.")
                elif msg == "__FAIL__":
                    self.inject_button.configure(state=tk.NORMAL)
                    messagebox.showerror("Injection failed", "Check the log for details.")
                else:
                    self._append_log(msg)
        except queue.Empty:
            pass
        self.after(100, self._poll_messages)

    def _append_log(self, text: str) -> None:
        self.log.insert(tk.END, text)
        self.log.see(tk.END)

    def _open_output_folder(self) -> None:
        output = Path(self.output_var.get())
        folder = output.parent if output.parent.exists() else ROOT
        import os

        os.startfile(folder)

    def _scan_classes(self) -> None:
        try:
            classes = list_classes(Path(self.donor_var.get()))
            self.class_choices = classes
            self.class_combo.configure(values=classes)
            if classes and self.class_var.get() not in classes:
                self.class_var.set(classes[0])
            self._append_log(f"Found {len(classes)} donor classes.\n")
        except Exception as exc:
            messagebox.showerror("Class scan failed", str(exc))

    def _use_donor_entrypoint(self) -> None:
        try:
            entrypoint_key = self.entrypoint_var.get().strip() or "main"
            found = read_entrypoint_classes(Path(self.donor_var.get()), entrypoint_key)
            if not found:
                messagebox.showinfo("No entrypoint", f"No donor entrypoint found for {entrypoint_key}.")
                return
            self.class_var.set(found[0])
            self._append_log(f"Using donor entrypoint {entrypoint_key}: {found[0]}\n")
        except Exception as exc:
            messagebox.showerror("Entrypoint read failed", str(exc))

    def _start_generate_donor(self) -> None:
        self._append_log("Generating IT WORKS donor...\n")
        thread = threading.Thread(target=self._generate_donor, daemon=True)
        thread.start()

    def _generate_donor(self) -> None:
        try:
            donor_path = create_it_works_donor(ROOT / "generated-donors")
            self.donor_var.set(str(donor_path))
            self.class_var.set("com.example.itworks.ItWorksMod")
            self.messages.put(f"Generated donor: {donor_path}\n")
            self.messages.put("Class: com.example.itworks.ItWorksMod\n")
        except Exception as exc:
            self.messages.put(f"ERROR: donor generation failed: {exc}\n")

    def _verify_output(self, output_jar: Path, class_name: str, entrypoint_key: str) -> list[str]:
        class_path = class_to_path(class_name)
        lines = ["Verification:"]
        with zipfile.ZipFile(output_jar, "r") as jar:
            names = set(jar.namelist())
            data = json.loads(jar.read("fabric.mod.json").decode("utf-8"))
            entrypoints = data.get("entrypoints", {}).get(entrypoint_key, [])
            if isinstance(entrypoints, str):
                entrypoints = [entrypoints]
            lines.append(f"  class present: {class_path in names}")
            lines.append(f"  entrypoint present: {class_name in entrypoints}")
            lines.append(f"  java dependency: {data.get('depends', {}).get('java', '(unchanged)')}")
        return lines

    def _backup_path(self, target: Path) -> Path:
        candidate = target.with_suffix(target.suffix + ".bak")
        index = 1
        while candidate.exists():
            candidate = target.with_suffix(target.suffix + f".bak{index}")
            index += 1
        return candidate


def list_classes(jar_path: Path) -> list[str]:
    with zipfile.ZipFile(jar_path, "r") as jar:
        return sorted(
            name[:-6].replace("/", ".")
            for name in jar.namelist()
            if name.endswith(".class") and not name.endswith("module-info.class")
        )


def read_entrypoint_classes(jar_path: Path, entrypoint_key: str) -> list[str]:
    with zipfile.ZipFile(jar_path, "r") as jar:
        data = json.loads(jar.read("fabric.mod.json").decode("utf-8"))
    value = data.get("entrypoints", {}).get(entrypoint_key, [])
    if isinstance(value, str):
        return [value]
    if isinstance(value, list):
        return [item for item in value if isinstance(item, str)]
    return []


def create_it_works_donor(output_dir: Path) -> Path:
    build_dir = output_dir / "it-works-build"
    classes_dir = build_dir / "classes"
    src_dir = build_dir / "src"
    donor_jar = output_dir / "it-works-donor-1.0.0.jar"

    shutil.rmtree(build_dir, ignore_errors=True)
    classes_dir.mkdir(parents=True, exist_ok=True)
    (src_dir / "net" / "fabricmc" / "api").mkdir(parents=True, exist_ok=True)
    (src_dir / "com" / "example" / "itworks").mkdir(parents=True, exist_ok=True)
    output_dir.mkdir(parents=True, exist_ok=True)

    (src_dir / "net" / "fabricmc" / "api" / "ModInitializer.java").write_text(
        "package net.fabricmc.api;\npublic interface ModInitializer { void onInitialize(); }\n",
        encoding="utf-8",
    )
    (src_dir / "com" / "example" / "itworks" / "ItWorksMod.java").write_text(
        """package com.example.itworks;

import net.fabricmc.api.ModInitializer;

public final class ItWorksMod implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("============================================================");
        System.out.println("==========              IT WORKS              ==============");
        System.out.println("==========      INJECTED DONOR MOD RAN       ==============");
        System.out.println("============================================================");
    }
}
""",
        encoding="utf-8",
    )

    subprocess.run(
        [
            "javac",
            "--release",
            "25",
            "-d",
            str(classes_dir),
            str(src_dir / "net" / "fabricmc" / "api" / "ModInitializer.java"),
            str(src_dir / "com" / "example" / "itworks" / "ItWorksMod.java"),
        ],
        check=True,
        cwd=ROOT,
    )
    shutil.rmtree(classes_dir / "net", ignore_errors=True)
    (classes_dir / "fabric.mod.json").write_text(
        json.dumps(
            {
                "schemaVersion": 1,
                "id": "it-works-donor",
                "version": "1.0.0",
                "name": "IT WORKS Donor",
                "environment": "*",
                "entrypoints": {"main": ["com.example.itworks.ItWorksMod"]},
                "depends": {"fabricloader": ">=0.18.4", "minecraft": ">=1.21", "java": ">=25"},
            },
            indent=2,
        ),
        encoding="utf-8",
    )
    if donor_jar.exists():
        donor_jar.unlink()
    subprocess.run(["jar", "--create", "--file", str(donor_jar), "-C", str(classes_dir), "."], check=True, cwd=ROOT)
    return donor_jar


if __name__ == "__main__":
    InjectorGui().mainloop()
