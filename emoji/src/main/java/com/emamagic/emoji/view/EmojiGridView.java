package com.emamagic.emoji.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import java.util.List;

import com.emamagic.emoji.R;
import com.emamagic.emoji.model.Emoji;

public class EmojiGridView {
    View rootView;
    EmojisPopup mEmojiconPopup;
    private EmojiRecents mRecents;


    EmojiGridView(Context context, List<Emoji> emojis, EmojiRecents recents, EmojisPopup emojiconPopup, boolean useSystemDefault) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        mEmojiconPopup = emojiconPopup;
        rootView = inflater.inflate(R.layout.emojicon_grid, null);
        setRecents(recents);
        GridView gridView = rootView.findViewById(R.id.Emoji_GridView);

        EmojiAdapter mAdapter = new EmojiAdapter(rootView.getContext(), emojis, useSystemDefault);
        mAdapter.setEmojiClickListener(new OnEmojiClickedListener() {

            @Override
            public void onEmojiClicked(Emoji emoji) {
                if (mEmojiconPopup.onEmojiClickedListener != null) {
                    mEmojiconPopup.onEmojiClickedListener.onEmojiClicked(emoji);
                }
                if (mRecents != null) {
                    mRecents.addRecentEmoji(rootView.getContext(), emoji);
                }
            }
        });
        gridView.setAdapter(mAdapter);
    }

    private void setRecents(EmojiRecents recents) {
        mRecents = recents;
    }

    public interface OnEmojiClickedListener {
        void onEmojiClicked(Emoji emoji);
    }

}