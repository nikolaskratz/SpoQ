package com.example.nikolas.spotfiyusefirsttry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class PlaylistSelectAdapter extends BaseAdapter {

    private Context mContext;
    private List<Playlist> mPlaylists;

    public PlaylistSelectAdapter(Context context, List<Playlist> playlists) {
        this.mContext = context;
        this.mPlaylists = playlists;
    }

    @Override
    public int getCount() {
        return mPlaylists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Playlist playlist = mPlaylists.get(position);

        if(convertView == null){
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_list_item_playlist, null);
        }

        final ImageView playlistCover = (ImageView) convertView.findViewById(R.id.coverImagePlaylist);
        final TextView playlistName = (TextView) convertView.findViewById(R.id.playlistName);

        playlistCover.setImageBitmap(playlist.getPlaylistCover());
        playlistName.setText(playlist.getPlaylistName());

        return convertView;
    }
}
