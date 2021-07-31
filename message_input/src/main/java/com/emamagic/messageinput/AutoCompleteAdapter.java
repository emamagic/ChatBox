package com.emamagic.messageinput;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.MultiAutoCompleteTextView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import im.limoo.android.data.local.entity.WorkspaceMemberEntity;
import im.limoo.android.data.repository.model.UserFull;
import im.limoo.android.provider.image.ImageLoader;
import im.limoo.android.utils.span.MentionGroupSpan;

/**
 * adapter class for showing suggestions for mention
 */
public class AutoCompleteAdapter extends ArrayAdapter<AutoCompleteAdapter.Wrapper> implements Filterable {
    private ArrayList<Wrapper> suggestions = new ArrayList<>();
    private String conversationAvatarHash;
    private AvatarDrawable conversationAvatarDrawable;
    private List<UserFull> userFulls;

    public AutoCompleteAdapter(Context context) {
        super(context, R.layout.row_item_message_input_auto_complete, R.id.name);
    }

    public void setGroupAvatar(String conversationAvatarHash, AvatarDrawable conversationAvatarDrawable) {
        this.conversationAvatarHash = conversationAvatarHash;
        this.conversationAvatarDrawable = conversationAvatarDrawable;
    }

    public void setUsersAndWorkspaceMembers(Map<String, UserFull> userFullMap) {
        this.userFulls = new ArrayList<>(userFullMap.values());
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, @NotNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.row_item_message_input_auto_complete, parent, false);
        }
        Object item = getItem(position).item;
        if (item instanceof GroupMention) {
            String allConversationMembersString = getContext().getString(R.string.all_conversation_members);
            ((FontTextView) convertView.findViewById(R.id.name)).setText(allConversationMembersString);
            if (conversationAvatarHash != null || conversationAvatarDrawable != null)
                ImageLoader.getInstance().loadAvatar(convertView.findViewById(R.id.avatar),
                        conversationAvatarHash, null, null, null, conversationAvatarDrawable);
            else
                ImageLoader.getInstance().loadAvatar(convertView.findViewById(R.id.avatar), null,
                        allConversationMembersString);
        } else {
            UserFull userFull = (UserFull) item;
            ((FontTextView) convertView.findViewById(R.id.name)).setText(userFull.getDisplayName());
            ImageLoader.getInstance().loadAvatar(convertView.findViewById(R.id.avatar), userFull);
        }
        return convertView;
    }

    @NotNull
    @Override
    public Wrapper getItem(int position) {
        return suggestions.get(position);
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @NotNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (((Wrapper) resultValue).item instanceof UserFull)
                    return "@" + ((UserFull) ((Wrapper) resultValue).item).getUsername() + " ";
                else {
                    return "@" + MentionGroupSpan.GROUP_MENTION_TEXT + " ";
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint == null) return null;
                FilterResults filterResults = new FilterResults();
                ArrayList<UserFull> suggestions = new ArrayList<>();
                if (constraint.length() == 1) {
                    suggestions.addAll(userFulls);
                } else {
                    String s = constraint.subSequence(1, constraint.length()).toString();
                    for (UserFull userFull : userFulls) {
                        if (contains(userFull.getFirstName(), s)
                                || contains(userFull.getLastName(), s)
                                || contains(userFull.getNickname(), s)) {
                            suggestions.add(userFull);
                        } else {
                            WorkspaceMemberEntity workspaceMemberEntity = userFull.getWorkspaceMemberEntity();
                            if (workspaceMemberEntity != null)
                                if (contains(workspaceMemberEntity.getNickname(), s)) {
                                    suggestions.add(userFull);
                                }
                        }
                    }
                }
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            }

            private boolean contains(@Nullable String name, String constraint) {
                if (name == null) return false;
                return name.contains(constraint);
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (constraint == null) {
                    suggestions.clear();
                    notifyDataSetChanged();
                } else {
                    suggestions.clear();
                    if (results.count > 0) {
                        suggestions.add(new Wrapper<>(new GroupMention()));
                        for (UserFull userFull : ((ArrayList<UserFull>) results.values)) {
                            suggestions.add(new Wrapper<>(userFull));
                        }
                    }
                    notifyDataSetChanged();
                }
            }
        };
    }

    public static class MentionTokenizer implements MultiAutoCompleteTextView.Tokenizer {
        @Override
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;
            while (i > 0 && text.charAt(i - 1) != '@') {
                if (text.charAt(i - 1) == ' ') return cursor;
                i--;
            }
            if (i > 0 && text.charAt(i - 1) == '@') {
                if (i == 1) return i - 1;
                else if (text.charAt(i - 2) == ' ' || text.charAt(i - 2) == '\n')
                    return i - 1;
            }
            return cursor;
        }

        @Override
        public int findTokenEnd(CharSequence text, int cursor) {
            return text.length();
        }

        @Override
        public CharSequence terminateToken(CharSequence text) {
            return text;
        }
    }

    public class Wrapper<T> {
        T item;

        public Wrapper(T item) {
            this.item = item;
        }
    }

    public class GroupMention {
    }
}
