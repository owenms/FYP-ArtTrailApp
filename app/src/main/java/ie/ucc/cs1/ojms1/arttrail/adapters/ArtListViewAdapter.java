package ie.ucc.cs1.ojms1.arttrail.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ucc.cs1.ojms1.arttrail.R;
import ie.ucc.cs1.ojms1.arttrail.helpers.DatabaseHandler;

/**
 * Create the Art list fragment's layout and data from database content
 * Created by owen on 26/02/2015.
 */
public class ArtListViewAdapter extends CursorAdapter {

    public ArtListViewAdapter(Context context, Cursor cursor) {
        super(context,cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return layoutInflater.inflate(R.layout.art_content, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView artName = (TextView) view.findViewById(R.id.art_name);
        TextView artistName = (TextView) view.findViewById(R.id.artist_name);
        ImageView artImage = (ImageView) view.findViewById(R.id.imageView);

        String art = cursor.getString(cursor.getColumnIndex(DatabaseHandler.ART_NAME));
        String artist = cursor.getString(cursor.getColumnIndex(DatabaseHandler.ART_ARTIST));
        int picture = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.ART_PIC));

        artName.setText(art);
        artistName.setText(artist);
        artImage.setImageResource(picture);
    }

}