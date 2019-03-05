package com.example.stage_2;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder>{
    private Intent intent;
    private String[] menu_name_list;

    static class ViewHolder extends RecyclerView.ViewHolder{
        View menu_view;
        TextView menu_txt;
        ImageView right_imview;

        public ViewHolder(View view){
            super(view);
            menu_view=view;
            menu_txt=(TextView)view.findViewById(R.id.menu_txt);
            right_imview=(ImageView)view.findViewById(R.id.right_imview);
        }
    }

    public MenuAdapter(String[] amenu_data_list){
        menu_name_list=amenu_data_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType){
      View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item,parent,false);
      final ViewHolder holder=new ViewHolder(view);
      holder.menu_view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              int position=holder.getAdapterPosition();
              switch (position){
                  case 0:
                      intent =  new Intent(holder.menu_view.getContext(), ContactPeopleActivity.class);
                      break;
              }
              holder.menu_view.getContext().startActivity(intent);
          }
      });
      return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        String menu_name=menu_name_list[position];
        holder.menu_txt.setText(menu_name);
    }

    @Override
    public int getItemCount(){
        return menu_name_list.length;
    }
}
