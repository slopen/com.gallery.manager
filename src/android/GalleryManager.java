package com.gallery.manager;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;

import android.os.Environment;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;
import android.database.Cursor;
import android.content.Context;
import android.provider.MediaStore;


public class GalleryManager extends CordovaPlugin {

    private CallbackContext cbContext = null;

    @Override
    public boolean execute(
        String action,
        JSONArray data,
        CallbackContext callbackContext
    ) throws JSONException {

        this.cbContext = callbackContext;

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callbackContext.sendPluginResult(pluginResult);

        if (action.equals("requestGallery")){

            this.cordova.getThreadPool().execute(new Runnable() {
                public void run() {

                    String[] thumbColumns = { MediaStore.Video.Thumbnails.DATA,
                        MediaStore.Video.Thumbnails.VIDEO_ID };

                    String[] mediaColumns = { MediaStore.Video.Media._ID,
                        MediaStore.Video.Media.DATA, MediaStore.Video.Media.TITLE,
                        MediaStore.Video.Media.MIME_TYPE, MediaStore.Video.Media.DURATION};

                    JSONArray videoList = new JSONArray();

                    Cursor cursor = cordova.getActivity().getApplicationContext()
                        .getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        mediaColumns, null, null, null);

                    if (cursor.moveToFirst()) {
                        do {

                            JSONObject videoItem = new JSONObject();
                            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));

                            videoItem.put("id", id);

                            Cursor thumbCursor = cordova.getActivity().getApplicationContext()
                                .getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);

                            if (thumbCursor.moveToFirst()) {
                                videoItem.put("thumbnail", thumbCursor.getString(thumbCursor
                                  .getColumnIndex(MediaStore.Video.Thumbnails.DATA)));
                            }

                            videoItem.put("path", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                            videoItem.put("title", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
                            videoItem.put("mimeType", cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
                            videoItem.put("duration", cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));

                            videoList.put(videoItem);

                        } while (cursor.moveToNext());

                    }

                    sendUpdate(videoList);
                }
            });
        }

        private void sendUpdate(String data) {
            PluginResult result = new PluginResult(PluginResult.Status.OK, data);
            result.setKeepCallback(true);
            this.cbContext.sendPluginResult(result);
        }

        return false;
    }
}
