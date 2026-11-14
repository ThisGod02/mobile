const STORAGE_KEY = "tasks_pwa_v1";
const list = document.querySelector("#taskList");
const form = document.querySelector("#addForm");
const input = document.querySelector("#taskInput");
const count = document.querySelector("#taskCount");
const empty = document.querySelector("#emptyState");
const filterButtons = [...document.querySelectorAll("[data-filter]")];
let filter = "all";
let tasks = loadTasks();

function loadTasks() {
  try { const value = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]"); return Array.isArray(value) ? value : []; }
  catch { return []; }
}
function persist() { localStorage.setItem(STORAGE_KEY, JSON.stringify(tasks)); }
function addTask(title) { tasks.unshift({ id: crypto.randomUUID?.() ?? String(Date.now()), title: title.trim(), completed: false, createdAt: Date.now() }); persist(); render(); }
function toggleTask(id) { tasks = tasks.map(task => task.id === id ? { ...task, completed: !task.completed } : task); persist(); render(); }
function deleteTask(id) { tasks = tasks.filter(task => task.id !== id); persist(); render(); }
function render() {
  const visible = tasks.filter(task => filter === "all" || (filter === "active" ? !task.completed : task.completed));
  list.replaceChildren();
  for (const task of visible) {
    const row = document.createElement("li"); row.className = `task-item${task.completed ? " completed" : ""}`;
    const checkbox = document.createElement("input"); checkbox.type = "checkbox"; checkbox.checked = task.completed; checkbox.setAttribute("aria-label", `Выполнено: ${task.title}`); checkbox.addEventListener("change", () => toggleTask(task.id));
    const title = document.createElement("span"); title.className = "task-title"; title.textContent = task.title;
    const remove = document.createElement("button"); remove.className = "task-delete"; remove.type = "button"; remove.textContent = "×"; remove.setAttribute("aria-label", `Удалить: ${task.title}`); remove.addEventListener("click", () => deleteTask(task.id));
    row.append(checkbox, title, remove); list.append(row);
  }
  const active = tasks.filter(task => !task.completed).length;
  const mod10 = active % 10; const mod100 = active % 100;
  const noun = mod10 === 1 && mod100 !== 11 ? "задача" : mod10 >= 2 && mod10 <= 4 && (mod100 < 12 || mod100 > 14) ? "задачи" : "задач";
  count.textContent = `${active} ${noun}`;
  empty.hidden = visible.length !== 0;
}
form.addEventListener("submit", event => { event.preventDefault(); if (input.value.trim()) addTask(input.value); input.value = ""; input.focus(); });
filterButtons.forEach(button => button.addEventListener("click", () => { filter = button.dataset.filter; filterButtons.forEach(item => item.classList.toggle("active", item === button)); render(); }));
document.querySelector("#clearCompleted").addEventListener("click", () => { tasks = tasks.filter(task => !task.completed); persist(); render(); });
function updateNetworkStatus() { const offline = !navigator.onLine; document.querySelector("#onlineStatus").textContent = offline ? "● Офлайн — данные сохранены" : "● Онлайн"; document.querySelector("#status").classList.toggle("offline", offline); }
window.addEventListener("online", updateNetworkStatus); window.addEventListener("offline", updateNetworkStatus);
if ("serviceWorker" in navigator) window.addEventListener("load", () => navigator.serviceWorker.register("./sw.js").catch(console.error));
render(); updateNetworkStatus();
