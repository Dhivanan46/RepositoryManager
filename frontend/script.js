fetch("dependencies.json")
  .then(res => res.json())
  .then(deps => {
    const list = document.getElementById("dependency-list");
    deps.forEach(dep => {
      const card = document.createElement("div");
      card.className = "card";
      card.innerHTML = `
        <h3>${dep.artifactId}</h3>
        <div class="coords">${dep.groupId}:${dep.artifactId}:${dep.version}</div>
        <p>${dep.description}</p>
        <a href="${dep.jarPath}" download>Download jar</a>
      `;
      list.appendChild(card);
    });
  });
