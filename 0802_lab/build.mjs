import { cp, mkdir, rm } from "node:fs/promises";
const files = ["index.html", "manifest.json", "sw.js", "css", "js", "icons"];
await rm("www", { recursive: true, force: true });
await mkdir("www", { recursive: true });
for (const file of files) await cp(file, `www/${file}`, { recursive: true });
console.log("PWA copied to www/ for Capacitor.");
