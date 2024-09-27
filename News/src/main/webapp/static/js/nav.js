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

// document.addEventListener("DOMContentLoaded", () => {
//   const main_nav = document.querySelector("nav.main ul");
//   const contentDiv = document.getElementById("content");
//   const loadingDiv = document.querySelector(".loading");

//   const onNavClickHandler = (e) => {
//     const target = e.target;
//     if (target.tagName !== "LI") return;

//     let url = `${rootPath}`;
//     if (target.classList.contains("news")) {
//       url += "/news/list";
//     } else if (target.classList.contains("media")) {
//       url += "/news/media";
//     }

//     // 로딩 이미지 표시
//     loadingDiv.style.display = "block";
//     contentDiv.style.display = "none";

//     // AJAX 요청
//     fetch(url)
//       .then((response) => {
//         if (!response.ok) {
//           throw new Error("Network response was not ok");
//         }
//         return response.text();
//       })
//       .then((data) => {
//         contentDiv.innerHTML = data; // 응답으로 받은 HTML을 삽입
//         contentDiv.style.display = "block"; // 콘텐츠 표시
//         loadingDiv.style.display = "none"; // 로딩 이미지 숨김
//       })
//       .catch((error) => {
//         console.error("Error fetching data:", error);
//         loadingDiv.style.display = "none"; // 오류 발생 시 로딩 이미지 숨김
//       });
//   };

//   main_nav.addEventListener("click", onNavClickHandler);
// });
