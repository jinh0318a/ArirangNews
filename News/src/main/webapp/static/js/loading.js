document.addEventListener("DOMContentLoaded", async () => {
  const loadingElement = document.querySelector("div.loading");

  async function loadNews() {
    // 로딩 이미지 보이기
    loadingElement.style.display = "block";

    try {
      const response = await fetch(`${rootPath}/`); // API 엔드포인트에 맞게 수정
      if (!response.ok) throw new Error("네트워크 오류");

      const newsList = await response.json(); // JSON으로 변환
      displayNews(newsList); // 뉴스 표시 함수 호출
    } catch (error) {
      console.error("Error fetching news:", error);
      document.querySelector(".news-container .list").innerHTML =
        "<li>뉴스 로드 중 오류 발생</li>";
    } finally {
      // 로딩 이미지 숨기기
      loadingElement.style.display = "none";
    }
  }

  function displayNews(newsList) {
    const newsContainer = document.querySelector(".news-container .list");
    newsContainer.innerHTML = ""; // 기존 내용 삭제

    if (newsList.length === 0) {
      newsContainer.innerHTML = "<li>뉴스가 없습니다.</li>";
      return;
    }

    newsList.forEach((news) => {
      const newsItem = document.createElement("li");
      newsItem.classList.add("card", "news-item");
      newsItem.innerHTML = `
              <a href="/news/detail/${news.m_no}">
                <img src="${
                  news.thum_url || "/news/static/images/no-image.jpg"
                }" alt="썸네일" class="thumbnail" />
                <div class="card-body">
                  <span class="title">${news.title}</span>
                </div>
              </a>
            `;
      newsContainer.appendChild(newsItem);
    });
  }

  await loadNews(); // 뉴스 로드 함수 호출
});
