const state = {
  repositories: [],
  selectedRepository: null,
  selectedMethod: null
};

const elements = {
  serviceStatus: document.getElementById("serviceStatus"),
  serviceMeta: document.getElementById("serviceMeta"),
  metricsGrid: document.getElementById("metricsGrid"),
  repoList: document.getElementById("repoList"),
  repoFilter: document.getElementById("repoFilter"),
  methodSelect: document.getElementById("methodSelect"),
  methodSignature: document.getElementById("methodSignature"),
  selectedRepoLabel: document.getElementById("selectedRepoLabel"),
  argsInput: document.getElementById("argsInput"),
  responseOutput: document.getElementById("responseOutput"),
  errorList: document.getElementById("errorList"),
  invokeBtn: document.getElementById("invokeBtn"),
  sampleBtn: document.getElementById("sampleBtn"),
  refreshDashboardBtn: document.getElementById("refreshDashboardBtn"),
  refreshErrorsBtn: document.getElementById("refreshErrorsBtn")
};

async function fetchJson(url, options) {
  const response = await fetch(url, options);
  const data = await response.json();
  if (!response.ok) {
    throw new Error(data.error || `Request failed with status ${response.status}`);
  }
  return data;
}

function formatJson(value) {
  return JSON.stringify(value, null, 2);
}

function renderMetrics(health, counts, repositories) {
  const cards = [
    ["Status", health.status],
    ["Database", health.databaseProduct],
    ["Repositories", String(repositories.length)],
    ["Employees", String(counts.employees ?? 0)],
    ["Leave Records", String(counts.leaveRecords ?? 0)],
    ["Payroll Results", String(counts.payrollResults ?? 0)],
    ["Audit Logs", String(counts.securityAuditLogs ?? 0)]
  ];

  elements.metricsGrid.innerHTML = cards.map(([label, value]) => `
    <article class="metric-card">
      <div class="metric-label">${escapeHtml(label)}</div>
      <div class="metric-value">${escapeHtml(value)}</div>
    </article>
  `).join("");

  elements.serviceStatus.textContent = health.status;
  elements.serviceMeta.textContent = `${health.databaseProduct} ${health.databaseVersion} via ${health.driverName}`;
}

function renderRepositories() {
  const filter = elements.repoFilter.value.trim().toLowerCase();
  const visible = state.repositories.filter(repo =>
    !filter ||
    repo.name.toLowerCase().includes(filter) ||
    repo.description.toLowerCase().includes(filter)
  );

  if (!visible.length) {
    elements.repoList.innerHTML = `<div class="repo-description">No repositories matched that filter.</div>`;
    return;
  }

  elements.repoList.innerHTML = visible.map(repo => `
    <article class="repo-card ${state.selectedRepository?.name === repo.name ? "active" : ""}" data-repo="${repo.name}">
      <div class="repo-name">${escapeHtml(repo.name)}</div>
      <div class="repo-description">${escapeHtml(repo.description)}</div>
      <div class="repo-method-count">${repo.methods.length} method(s)</div>
    </article>
  `).join("");

  document.querySelectorAll(".repo-card").forEach(card => {
    card.addEventListener("click", () => selectRepository(card.dataset.repo));
  });
}

function selectRepository(name) {
  state.selectedRepository = state.repositories.find(repo => repo.name === name) || null;
  state.selectedMethod = state.selectedRepository?.methods?.[0] || null;
  elements.selectedRepoLabel.textContent = state.selectedRepository ? state.selectedRepository.name : "No repository selected";
  renderRepositories();
  renderMethodSelect();
  applySampleArgs();
}

function renderMethodSelect() {
  const repo = state.selectedRepository;
  if (!repo) {
    elements.methodSelect.innerHTML = "";
    elements.methodSignature.textContent = "Select a repository to see methods.";
    return;
  }

  elements.methodSelect.innerHTML = repo.methods.map((method, index) => `
    <option value="${index}">${method.name}</option>
  `).join("");

  elements.methodSelect.value = String(repo.methods.findIndex(method => method === state.selectedMethod) || 0);
  elements.methodSelect.onchange = () => {
    state.selectedMethod = repo.methods[Number(elements.methodSelect.value)] || repo.methods[0];
    updateMethodSignature();
  };

  if (!state.selectedMethod) {
    state.selectedMethod = repo.methods[0] || null;
  }
  updateMethodSignature();
}

function updateMethodSignature() {
  if (!state.selectedMethod) {
    elements.methodSignature.textContent = "This repository has no exposed methods.";
    return;
  }

  const params = state.selectedMethod.parameters.map(param => `${param.name}: ${param.type}`).join(", ");
  elements.methodSignature.textContent =
    `${state.selectedMethod.declaringInterface}.${state.selectedMethod.name}(${params}) -> ${state.selectedMethod.returnType}`;
}

function applySampleArgs() {
  const count = state.selectedMethod?.parameters?.length || 0;
  const sample = Array.from({ length: count }, (_, index) => `arg${index}`);
  elements.argsInput.value = formatJson(sample);
}

function renderErrors(errors) {
  if (!errors.length) {
    elements.errorList.innerHTML = `<div class="repo-description">No database errors have been logged recently.</div>`;
    return;
  }

  elements.errorList.innerHTML = errors.map(error => `
    <article class="error-card">
      <div class="error-title">${escapeHtml(error.operation || "Unknown operation")}</div>
      <div class="error-meta">${escapeHtml(error.timestamp || "No timestamp")} • ${escapeHtml(error.outcome || "UNKNOWN")}</div>
      <div class="error-details">${escapeHtml(error.details || "No details available")}</div>
    </article>
  `).join("");
}

async function loadDashboard() {
  const dashboard = await fetchJson("/api/dashboard");
  renderMetrics(dashboard.health, dashboard.counts, dashboard.repositories);
  state.repositories = dashboard.repositories || [];
  if (!state.selectedRepository && state.repositories.length) {
    state.selectedRepository = state.repositories[0];
    state.selectedMethod = state.selectedRepository.methods[0] || null;
  }
  renderRepositories();
  renderMethodSelect();
  renderErrors(dashboard.recentErrors || []);
}

async function loadErrors() {
  const errors = await fetchJson("/api/errors");
  renderErrors(errors);
}

async function invokeSelectedMethod() {
  if (!state.selectedRepository || !state.selectedMethod) {
    elements.responseOutput.textContent = "Pick a repository and method first.";
    return;
  }

  let args;
  try {
    args = JSON.parse(elements.argsInput.value || "[]");
    if (!Array.isArray(args)) {
      throw new Error("Arguments must be a JSON array.");
    }
  } catch (error) {
    elements.responseOutput.textContent = error.message;
    return;
  }

  elements.responseOutput.textContent = "Calling gateway...";
  try {
    const response = await fetchJson("/api/invoke", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        repository: state.selectedRepository.name,
        method: state.selectedMethod.name,
        args
      })
    });
    elements.responseOutput.textContent = formatJson(response);
  } catch (error) {
    elements.responseOutput.textContent = error.message;
  }
}

function escapeHtml(value) {
  return String(value)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll("\"", "&quot;")
    .replaceAll("'", "&#39;");
}

elements.repoFilter.addEventListener("input", renderRepositories);
elements.invokeBtn.addEventListener("click", invokeSelectedMethod);
elements.sampleBtn.addEventListener("click", applySampleArgs);
elements.refreshDashboardBtn.addEventListener("click", loadDashboard);
elements.refreshErrorsBtn.addEventListener("click", loadErrors);

loadDashboard().catch(error => {
  elements.serviceStatus.textContent = "DOWN";
  elements.serviceMeta.textContent = error.message;
  elements.responseOutput.textContent = error.message;
});
