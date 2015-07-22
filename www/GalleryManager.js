/*global cordova, module*/

module.exports = {
    requestGallery: function (successCallback, errorCallback) {
        cordova.exec(function(result){
            var videoList = [];
            try{
                videoList = JSON.parse(result);
            }catch(e){}

            successCallback(videoList);

        }, errorCallback, "GalleryManager", "requestGallery", []);
    }
};
