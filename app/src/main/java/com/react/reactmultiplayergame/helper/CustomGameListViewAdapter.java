package com.react.reactmultiplayergame.helper;

/**
 * Created by gan on 6/5/17.
 */

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.react.reactmultiplayergame.R;

public class CustomGameListViewAdapter extends ArrayAdapter<CustomGameListViewModel> {

    private final List<CustomGameListViewModel> list;
    private final Activity context;

    public CustomGameListViewAdapter(Activity context, List<CustomGameListViewModel> list) {
        super(context, R.layout.customgame_listview, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected ImageView checkbox;
        protected AutoResizeTextView sequenceNumberTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.customgame_listview, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.customgame_textView);
            viewHolder.checkbox = (ImageView) view.findViewById(R.id.customgame_checkboxTick);
            viewHolder.sequenceNumberTextView = (AutoResizeTextView) view.findViewById(R.id.customgame_checkboxNumber);
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getName());


       // only set checkbox if randomizeOrder is on
        if(Utils.getCustomGameRandomizeOrderOn(context)) {
            // this is how we set checkbox to be checked or not checked. Cant add imageview programmatically, as it will get reused in subsequent recyclerview pass
            // therefore no choice but to use this visibility method
            if (((CustomGameListViewModel) holder.checkbox.getTag()).isSelected() >0) {
                holder.checkbox.setVisibility(View.VISIBLE);
            } else {
                holder.checkbox.setVisibility(View.GONE);
            }
            // need to set sequenceTextView to gone becoz we are showing tick
            holder.sequenceNumberTextView.setVisibility(View.GONE);
        }
        else {
            // else we set in sequence order
            if (((CustomGameListViewModel) holder.checkbox.getTag()).isSelected() >0) {
                // we make use of the checkbox.getTag to determine if it is selected ornot. No need create separate .getTag, since
                // sequence order is using same mechanism as random order, in the sence it just need to determine if it is selected ornot
                holder.sequenceNumberTextView.setVisibility(View.VISIBLE);
                holder.sequenceNumberTextView.setText("" + list.get(position).getSequenceNumber());
            } else {
                holder.sequenceNumberTextView.setVisibility(View.GONE);
            }
            holder.checkbox.setVisibility(View.GONE);
        }

        return view;
    }
}
