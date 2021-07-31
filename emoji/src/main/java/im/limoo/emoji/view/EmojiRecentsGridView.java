package im.limoo.emoji.view;

import android.content.Context;
import android.widget.GridView;

import java.util.List;

import im.limoo.emoji.EmojiRecentsManager;
import im.limoo.emoji.R;
import im.limoo.emoji.model.Emoji;

public class EmojiRecentsGridView extends EmojiGridView implements EmojiRecents {
    private EmojiAdapter mAdapter;

    EmojiRecentsGridView(Context context, List<Emoji> emojis,
                         EmojiRecents recents, EmojisPopup emojisPopup, boolean useSystemDefault) {
        super(context, EmojiRecentsManager.getInstance(context), recents, emojisPopup, useSystemDefault);
        mAdapter = new EmojiAdapter(rootView.getContext(), EmojiRecentsManager.getInstance(context), useSystemDefault);
        mAdapter.setEmojiClickListener(new OnEmojiClickedListener() {

            @Override
            public void onEmojiClicked(Emoji emoji) {
                if (mEmojiconPopup.onEmojiClickedListener != null) {
                    mEmojiconPopup.onEmojiClickedListener.onEmojiClicked(emoji);
                }
            }
        });
        GridView gridView = rootView.findViewById(R.id.Emoji_GridView);
        gridView.setAdapter(mAdapter);
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void addRecentEmoji(Context context, Emoji emojicon) {
        EmojiRecentsManager recents = EmojiRecentsManager
                .getInstance(context);
        recents.push(emojicon);

        // notify dataset changed
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

}