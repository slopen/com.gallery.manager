/*global cordova, module*/

module.exports = {
    requestGallery: function (successCallback, errorCallback) {
        cordova.exec(successCallback, errorCallback, "GalleryManager", "requestGallery", []);
    }
};
