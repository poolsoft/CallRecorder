package ramt57.infotrench.com.callrecorder.adapter;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ramt57.infotrench.com.callrecorder.R;
import ramt57.infotrench.com.callrecorder.contacts.ContactProvider;
import ramt57.infotrench.com.callrecorder.pojo_classes.Contacts;

public class RecyclerAdapter extends RecyclerView.Adapter {
    private static ArrayList<Object> contacts=new ArrayList<>();
    private final int VIEW1 = 0, VIEW2 = 1,VIEW3=3;
    static OnitemClickListener listener;
    Context ctx;
    public RecyclerAdapter(){

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder1;
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW1:
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.people_contact,parent,false);
                viewHolder1 = new MyViewHolder(view);
                ctx=view.getContext();
                break;
            case VIEW2:
                View v2 = inflater.inflate(R.layout.no_contact_list,parent, false);
                viewHolder1 = new MyViewHolder(v2);
                ctx=v2.getContext();
                break;
            case VIEW3:
                View v3=inflater.inflate(R.layout.time_row,parent,false);
                viewHolder1=new MytimeViewHolder(v3);
                ctx=v3.getContext();
               break;
            default:
                View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                viewHolder1 = new MyViewHolder(v);
                ctx=v.getContext();
                break;
        }
        return  viewHolder1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case VIEW1:
                Contacts contact= (Contacts) contacts.get(position);
                if(ContactProvider.checkFav(ctx,contact.getNumber())){
                    //not favourite
                    ((MyViewHolder)holder).favorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                }else{
                    //favourite
                    ((MyViewHolder)holder).favorite.setImageResource(R.drawable.ic_star_black_24dp);
                }
                if(ContactProvider.checkContactToRecord(ctx,contact.getNumber())){
                    //record
                    ((MyViewHolder)holder).state.setImageResource(R.drawable.ic_microphone);
                }else{
                    //dont wanna record
                    ((MyViewHolder)holder).state.setImageResource(R.drawable.ic_muted);
                }
                ((MyViewHolder)holder).name.setText(contact.getName());
                ((MyViewHolder)holder).number.setText(contact.getNumber());
                if(contact.getPhotoUri()!=null){
                    Glide.with(ctx).load(contact.getPhotoUri()).into(((MyViewHolder) holder).profileimage);
                }else {
                    ((MyViewHolder)holder).profileimage.setImageResource(R.drawable.profile);
                }
                ((MyViewHolder)holder).time.setText(contact.getTime());
                break;
            case VIEW2:
                Contacts contact3= (Contacts) contacts.get(position);
                if(ContactProvider.checkFav(ctx,contact3.getNumber())){
                    //not favourite
                    ((MyViewHolder)holder).favorite.setImageResource(R.drawable.ic_star_border_black_24dp);
                }else{
                    //favourite
                    ((MyViewHolder)holder).favorite.setImageResource(R.drawable.ic_star_black_24dp);
                }
                if(ContactProvider.checkContactToRecord(ctx,contact3.getNumber())){
                    //record
                    ((MyViewHolder)holder).state.setImageResource(R.drawable.ic_microphone);
                }else{
                    //dont wanna record
                    ((MyViewHolder)holder).state.setImageResource(R.drawable.ic_muted);
                }
                ((MyViewHolder)holder).name.setText(contact3.getNumber());
                ((MyViewHolder)holder).time.setText(contact3.getTime());
                break;
            case VIEW3:
                String time=contacts.get(position).toString();
                if(time=="1"){
                    ((MytimeViewHolder)holder).time.setText("Today");
                }else if(time=="2"){
                    ((MytimeViewHolder)holder).time.setText("Yesterday");
                }else{
                    ((MytimeViewHolder)holder).time.setText(time);
                }
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileimage;
        TextView name;
        TextView number;
        TextView time;
        ImageView state,favorite;
        public MyViewHolder(View itemView) {
            super(itemView);
            profileimage=(CircleImageView)itemView.findViewById(R.id.profile_image);
            name=(TextView)itemView.findViewById(R.id.textView2);
            number=(TextView)itemView.findViewById(R.id.textView3);
            favorite=(ImageView)itemView.findViewById(R.id.imageView);
            time=(TextView)itemView.findViewById(R.id.textView4);
            state=(ImageView)itemView.findViewById(R.id.imageView5);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(view,getAdapterPosition());
                }
            });
        }

    }
    public static class MytimeViewHolder extends RecyclerView.ViewHolder{
        TextView time;
        public MytimeViewHolder(View itemView) {
            super(itemView);
            time=(TextView)itemView.findViewById(R.id.time_view);
        }

    }
    @Override
    public int getItemViewType(int position) {
        if(contacts.get(position) instanceof String){
            return VIEW3;
        }else{
            if(contacts.get(position) instanceof Contacts){
                Contacts contxt= (Contacts) contacts.get(position);
                if(contxt.getName()!=null){
                    return VIEW1;
                }else{
                    return VIEW2;
                }
            }else {
                return VIEW1;
            }
        }
    }
    public void setContacts(ArrayList<Object> contacts){
            RecyclerAdapter.contacts=contacts;
    }
    public  void setListener(RecyclerAdapter.OnitemClickListener listener) {
        RecyclerAdapter.listener = listener;
    }
    public interface OnitemClickListener{
        public void onClick(View v,int position);
        void onLongClick(View view, int position);
    }
}
