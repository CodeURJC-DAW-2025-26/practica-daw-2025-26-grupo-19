let NUM_RESULTS = 3;
let loadMoreRequest = 0;

async function loadMore() {
    // LoadMore Torneos
    const from = NUM_RESULTS * (loadMoreRequest+1);
    const to = from+NUM_RESULTS;

    let response = await fetch(`/torneos?from=${from}&to=${to}`);
    let responseTxt = await response.text();

    let leaguesDiv = document.getElementById('leaguesDiv');
    leaguesDiv.innerHTML+=responseTxt;

    loadMoreRequest++;

    // LoadMore Button
    let BtnResponse = await fetch(`/showLoadMore?to=${to}`);
    let responseObj = await BtnResponse.json();
    let loadMoreButton = document.getElementById("loadMoreButton");

    if(responseObj.showButton == false) {
        loadMoreButton.style.display = 'none';
    }
    else {
        loadMoreButton.style.display = 'block';
    }
    
}