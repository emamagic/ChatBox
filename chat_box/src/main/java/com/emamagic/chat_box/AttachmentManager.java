package com.emamagic.chat_box;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AttachmentManager {

    public static void selectDocument(Activity activity, int requestCode) {
        Permissions.with(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .ifNecessary()
                .withPermanentDenialDialog(activity.getString(R.string.AttachmentManager_requires_the_external_storage_permission_in_order_to_attach_photos_videos_or_audio))
                .onAllGranted(() -> selectMediaType(activity, "*/*", null, requestCode))
                .execute();
    }

    public static void selectGallery(Activity activity, int requestCode) {
        Permissions.with(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .ifNecessary()
                .withPermanentDenialDialog(activity.getString(R.string.AttachmentManager_requires_the_external_storage_permission_in_order_to_attach_photos_videos_or_audio))
                .onAllGranted(() -> selectMediaType(activity, "image/*", new String[]{"image/*", "video/*"}, requestCode))
                .execute();
    }


    public static void selectImage(Activity activity, int requestCode) {
        Permissions.with(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .ifNecessary()
                .withPermanentDenialDialog(activity.getString(R.string.AttachmentManager_requires_the_external_storage_permission_in_order_to_attach_photos_videos_or_audio))
                .onAllGranted(() -> selectMediaType(activity, "image/*", new String[]{"image/*"}, requestCode))
                .execute();
    }

    public static void selectAudio(Activity activity, int requestCode) {
        Permissions.with(activity)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .ifNecessary()
                .withPermanentDenialDialog(activity.getString(R.string.AttachmentManager_requires_the_external_storage_permission_in_order_to_attach_photos_videos_or_audio))
                .onAllGranted(() -> selectMediaType(activity, "audio/*", null, requestCode))
                .execute();
    }

    public static void selectContactInfo(Activity activity, int requestCode) {
        Permissions.with(activity)
                .request(Manifest.permission.WRITE_CONTACTS)
                .ifNecessary()
                .withPermanentDenialDialog(activity.getString(R.string.AttachmentManager_requires_contacts_permission_in_order_to_attach_contact_information))
                .onAllGranted(() -> {
                    Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    activity.startActivityForResult(intent, requestCode);
                })
                .execute();
    }

    public static void capturePhoto(Activity activity, int requestCode, Uri captureUri) {
        Permissions.with(activity)
                .request(Manifest.permission.CAMERA)
                .ifNecessary()
                .withPermanentDenialDialog(activity.getString(R.string.AttachmentManager_requires_the_camera_permission_in_order_to_take_photos_but_it_has_been_permanently_denied))
                .onAllGranted(() -> {
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (captureIntent.resolveActivity(activity.getPackageManager()) != null) {
                        //Log.i("captureUri path is " + captureUri.getPath());
                        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, captureUri);
                        activity.startActivityForResult(captureIntent, requestCode);
                    }
                })
                .execute();
    }

    private static void selectMediaType(Activity activity, @NonNull String type,
                                        @Nullable String[] extraMimeType, int requestCode) {
        final Intent intent = new Intent();
        intent.setType(type);

        if (extraMimeType != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, extraMimeType);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
            try {
                activity.startActivityForResult(intent, requestCode);
                return;
            } catch (ActivityNotFoundException anfe) {
                Log.e("AttachmentManager", "couldn't complete ACTION_OPEN_DOCUMENT, no activity found. falling back.");
            }
        }

        intent.setAction(Intent.ACTION_GET_CONTENT);

        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException anfe) {
            Log.e("AttachmentManager","couldn't complete ACTION_GET_CONTENT intent, no activity found. falling back.");
            Toast.makeText(activity, R.string.AttachmentManager_cant_open_media_selection, Toast.LENGTH_LONG).show();
        }
    }

}
