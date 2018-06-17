package com.venus.app.Modele;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import org.json.JSONObject;

/**
 * Created by arnold on 12/03/18.
 */
public interface Modele {
    JSONObject toJSON();
    RecyclerView.ViewHolder toView(ViewGroup parent);
}
