package org.devheap.intempore;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.devheap.intempore.route.RoutePoint;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ekaterina on 10/13/17.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.RouteViewHolder> {
    List<RoutePoint> routePoints;

    RVAdapter(List<RoutePoint> routePoints){
        this.routePoints = routePoints;
    }

    RVAdapter(RoutePoint[] routePoints){
        this.routePoints = Arrays.asList(routePoints);
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entity_all_points, parent, false);
        RouteViewHolder routeViewHolder = new RouteViewHolder(view);
        return routeViewHolder;
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        holder.textView.setText(routePoints.get(position).getDetails().name);
    }


    @Override
    public int getItemCount() {
        return routePoints.size();
    }

    public void removeItem(int position){
        routePoints.remove(position);
        notifyItemRemoved(position);
    }

    public static class RouteViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        Button delete;
        public RouteViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.entity_text_view);
        }
    }
}
