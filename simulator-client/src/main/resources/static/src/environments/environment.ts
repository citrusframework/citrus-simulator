const LOCAL_STORAGE_ENV_KEY = '$CITRUS_ADMIN';

let userEnv = {};
if(localStorage) {
    const localStorageContent = localStorage.getItem(LOCAL_STORAGE_ENV_KEY);
    try {
        userEnv = JSON.parse(localStorageContent) || {};
    } catch(e) {
        console.warn(`Found item in '${LOCAL_STORAGE_ENV_KEY}' but could not parse it's contents: `, localStorageContent, e)
    }
}

export const environment = {
    production: false,
    traceRouting: true,
    reduxTools: true,
    ...userEnv
};

