package uci.cisol.apkinventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import java.util.List;

public class HardwareAdapter extends RecyclerView.Adapter<HardwareAdapter.ViewHolder> {

    private List<HardwareItem> hardwareList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HardwareItem item);
    }

    public HardwareAdapter(List<HardwareItem> hardwareList, OnItemClickListener listener) {
        this.hardwareList = hardwareList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_hardware_item, parent, false);
        return new ViewHolder(view);
    }

    public void updateList(List<HardwareItem> newList) {
        hardwareList = newList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HardwareItem item = hardwareList.get(position);
        holder.hardwareHwid.setText("Equipo: " + item.getHwid());
        holder.hardwareMotherboard.setText("Motherboard: " + item.getMotherboard());
        holder.hardwareProcesador.setText("Procesador: " + item.getProcesador());
        holder.hardwareRam.setText("RAM: " + item.getRam());
        holder.hardwareAlmacenamiento.setText("Almacenamiento: "+ item.getAlmacenamiento());
        holder.hardwareVideo.setText("Video: " + item.getVideo());
        holder.hardwareScanner.setText("Scanner: " + item.getScanner());
        holder.hardwareImpresoras.setText("Impresoras: " + item.getImpresoras());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return hardwareList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView hardwareHwid;
        TextView hardwareMotherboard;
        TextView hardwareProcesador;
        TextView hardwareRam;
        TextView hardwareAlmacenamiento;
        TextView hardwareVideo;
        TextView hardwareScanner;
        TextView hardwareImpresoras;
        TextView itemNumber;

        ViewHolder(View itemView) {
            super(itemView);
            hardwareHwid = itemView.findViewById(R.id.hardware_hwid);
            hardwareMotherboard = itemView.findViewById(R.id.hardware_motherboard);
            hardwareProcesador = itemView.findViewById(R.id.hardware_procesador);
            hardwareRam = itemView.findViewById(R.id.hardware_ram);
            hardwareAlmacenamiento = itemView.findViewById(R.id.hardware_almacenamiento);
            hardwareVideo = itemView.findViewById(R.id.hardware_video);
            hardwareScanner = itemView.findViewById(R.id.hardware_scanner);
            hardwareImpresoras = itemView.findViewById(R.id.hardware_impresoras);
            itemNumber = itemView.findViewById(R.id.item_number);
        }
    }
}
