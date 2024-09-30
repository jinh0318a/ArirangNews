document.addEventListener("DOMContentLoaded", () => {
  const main_nav = document.querySelector("nav.main ul");
  const onNavClickHandler = (e) => {
    const target = e.target;
    if (target.tagName !== "LI") return false;

    // 각 tag 의 class 이름을 배열로 return
    const classList = target.classList;

    let url = `${rootPath}`;

    if (classList.contains("news")) url += "/news/list";
    if (classList.contains("media")) url += "/news/media";

    document.location.href = url;
  };

  main_nav.addEventListener("click", onNavClickHandler);
});
