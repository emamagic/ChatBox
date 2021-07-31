package im.limoo.emoji.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import im.limoo.emoji.R;
import im.limoo.emoji.model.Emoji;

class EmojiAdapter extends ArrayAdapter<Emoji> {
    EmojiGridView.OnEmojiClickedListener emojiClickListener;
    private boolean mUseSystemDefault = false;


    public EmojiAdapter(Context context, List<Emoji> data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    public EmojiAdapter(Context context, Emoji[] data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }


    public void setEmojiClickListener(EmojiGridView.OnEmojiClickedListener listener) {
        this.emojiClickListener = listener;
    }

    @NotNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = v.findViewById(R.id.emojicon_icon);
            holder.icon.setUseSystemDefault(mUseSystemDefault);

            v.setTag(holder);
        }

        Emoji emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(emoji.getShort_name());
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiClickListener.onEmojiClicked(getItem(position));
            }
        });

        return v;
    }

    class ViewHolder {
        EmojiTextView icon;
    }
}