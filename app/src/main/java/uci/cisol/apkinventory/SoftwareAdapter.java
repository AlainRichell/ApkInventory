package uci.cisol.apkinventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import java.util.List;

public class SoftwareAdapter extends RecyclerView.Adapter<SoftwareAdapter.ViewHolder> {

    private final List<SoftwareItem> softwareList;

    public SoftwareAdapter(List<SoftwareItem> softwareList) {
        this.softwareList = softwareList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_software_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SoftwareItem item = softwareList.get(position);
        holder.softwareNombre.setText(item.getNombre());
        holder.softwareVersion.setText(item.getVersion());
        holder.itemNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return softwareList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView softwareNombre;
        TextView softwareVersion;
        TextView itemNumber;

        ViewHolder(View itemView) {
            super(itemView);
            softwareNombre = itemView.findViewById(R.id.software_nombre);
            softwareVersion = itemView.findViewById(R.id.software_version);
            itemNumber = itemView.findViewById(R.id.item_number);
        }
    }
}
