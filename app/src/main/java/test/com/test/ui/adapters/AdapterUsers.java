package test.com.test.ui.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import test.com.test.R;
import test.com.test.model.User;
import test.com.test.net.ImageDownloaderTask;

/**
 * Created by s.bartashevich on 3/1/2017.
 */

public class AdapterUsers  extends RecyclerView.Adapter<AdapterUsers.ViewHolder>
    //class adapter for received list of users
    {

        private ArrayList<User> items;
        ViewHolderClickListener listener;
        Context context;

        public  AdapterUsers(ArrayList<User> list,ViewHolderClickListener lst,Context context)
        {
            items = list;
            listener=lst;
            this.context=context;

            ViewHolder.setViewHolderClickListener(new ViewHolderClickListener()
            {



                @Override
                public void onItemClick(int position)
                {
                    if(listener!=null){
                        listener.onItemClick(position);
                    }
                }

                @Override
                public void onItemLongClick(int position)
                {
                    if(listener!=null){
                        listener.onItemLongClick(position);
                    }
                }


            });
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_user_item, parent, false);

            return new ViewHolder(v);

        }



        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position)
        {
            final User item=items.get(position);
            holder.txtNavItemText.setText(item. getFullName());

            ImageDownloaderTask.BitmapListener listener=new ImageDownloaderTask.BitmapListener() {
                @Override
                public void onFailed() {

                }

                @Override
                public void onBitmapLoaded(Bitmap bitmap,int pos) {
                    if (pos==position) {
                        holder.imgNavItemIcon.setImageBitmap(bitmap);
                    }
                }
            };
            int valueInPixels = (int) context.getResources().getDimension(R.dimen.bitmap_image_max_size);
            new ImageDownloaderTask(listener,valueInPixels,item.getURL(),context,position).execute();
        }


        @Override
        public int getItemCount()
        {
            return items.size();
        }





        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
        {

            private TextView txtNavItemText;
            private ImageView imgNavItemIcon;
            private static ViewHolderClickListener viewHolderClickListener;

            public ViewHolder(View itemView)
            {
                super(itemView);


                itemView.setClickable(true);
                itemView.setOnClickListener(this);

                        txtNavItemText = (TextView) itemView.findViewById(R.id.text);
                        imgNavItemIcon = (ImageView) itemView.findViewById(R.id.icon);



            }

            public static void setViewHolderClickListener(ViewHolderClickListener viewHolderClickListener)
            {
                ViewHolder.viewHolderClickListener = viewHolderClickListener;
            }



            @Override
            public void onClick(View v)
            {
                viewHolderClickListener.onItemClick(getAdapterPosition());


            }
            @Override
            public boolean onLongClick(View v)
            {
                viewHolderClickListener.onItemLongClick(getAdapterPosition());

                return false;
            }
        }

        public interface ViewHolderClickListener
        {

            void onItemClick(int position);
            void onItemLongClick(int position);
        }
    }

