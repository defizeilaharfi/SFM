package com.example.sfmtesting;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.sfmtesting.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NPDWardahFragment extends Fragment {
    final com.example.sfmtesting.Spacecraft kumpulanorder = new com.example.sfmtesting.Spacecraft();
    final ArrayList<com.example.sfmtesting.Spacecraft> order = new ArrayList<com.example.sfmtesting.Spacecraft>();
    final ArrayList<Integer> orderedID = new ArrayList<Integer>();
    final ArrayList<String> orderedkode = new ArrayList<String>(); //kodemo
    final ArrayList<String> orderedname = new ArrayList<String>(); //namamo
    final ArrayList<String> orderedprice = new ArrayList<String>(); //hargamo
    final ArrayList<String> orderedstock = new ArrayList<String>(); //stockmo
    final ArrayList<String> orderedqty = new ArrayList<String>(); //qtymo
    final ArrayList<String> orderedcategory = new ArrayList<String>();
    ImageView scannpdwardah;
    public static SearchView mySearchView;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ArrayList<com.example.sfmtesting.Spacecraft> spacecrafts = new ArrayList<com.example.sfmtesting.Spacecraft>();
    ListView myListView;
    ListViewAdapter adapter;
    private ArrayList<String> stock1 = new ArrayList<String>();
    private ArrayList<String> qty1 = new ArrayList<String>();

    //    @NonNull
    @Override

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_npd_wardah, container, false);
        myListView = view.findViewById(R.id.mListNPDWardah);
        final ProgressBar myProgressBar = view.findViewById(R.id.myProgressBarNPDWardah);
        scannpdwardah = view.findViewById(R.id.barcodenpdwardah);
        scannpdwardah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), com.example.sfmtesting.ScannpdwardahActivity.class);
                startActivity(intent);
            }
        });
        mySearchView = view.findViewById(R.id.mySearchViewNPDWardah);
        mySearchView.setIconified(true);
        mySearchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        mySearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

        spacecrafts = new JSONDownloader(getActivity()).retrieve(myListView, myProgressBar);
        adapter = new ListViewAdapter(getActivity(), spacecrafts);
        String txt = "";
        String all = "";
        for (int j = 0; j < spacecrafts.size(); j++) {
            txt = spacecrafts.get(j).namaproduk + "\n";
            all = all + txt;

        }
//        Toast.makeText(getActivity(), "Order: \n" + all, Toast.LENGTH_LONG).show();
        myListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

//        if (state != null) {
//            myListView.onRestoreInstanceState(state);
//        }
        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
//                Toast.makeText(getActivity(), "data muncul", Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.form_takingorder, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Input Order");

                TextView formKode = null;
                final TextView formNama;
                final TextView formHarga;
                final TextView formPcs, formws;
                final EditText formStock, formQty;

                formKode = dialogView.findViewById(R.id.kode_odoo_form);
                formNama = dialogView.findViewById(R.id.nama_produk_form);
                formHarga = dialogView.findViewById(R.id.harga_form);
                formStock = dialogView.findViewById(R.id.stock_form);
                formws = dialogView.findViewById(R.id.ws_form);
                formQty = dialogView.findViewById(R.id.qty_form);
                formPcs = dialogView.findViewById(R.id.pcs_produk_form);

                final com.example.sfmtesting.Spacecraft coba = (com.example.sfmtesting.Spacecraft) adapter.getItem(position);

                String konsta = pref.getString("const", "2");
                final int konst = Integer.parseInt(konsta);

                formKode.setText(coba.getKodeodoo());
                formNama.setText(coba.getNamaproduk());
                formHarga.setText(coba.getPrice());
                formPcs.setText(coba.getKoli());
                formws.setText("" + coba.getWeeklySales());
                formStock.setHint(coba.getStock());
                String stockformawal = formStock.getText().toString();
                if (!stockformawal.isEmpty()) {
                    int intstockformawal = Integer.parseInt(stockformawal);
                    int qtyformawal = konst*coba.getWeeklySales() - intstockformawal;
                    if (qtyformawal >= 0) {
                        formQty.setHint(String.valueOf(qtyformawal));
                    } else {
                        formQty.setHint("0");
                    }
                } else {
                    formQty.setHint("0");
                }

                formStock.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        String stockform = formStock.getText().toString();
                        if (!stockform.isEmpty()) {
                            int intstockform = Integer.parseInt(stockform);
                            int qtyform = konst*coba.getWeeklySales() - intstockform;
                            if (qtyform >= 0) {
                                formQty.setHint(String.valueOf(qtyform));
                            } else {
                                formQty.setHint("0");
                            }
                        } else {
                            formQty.setHint("0");
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        String stockform = formStock.getText().toString();
                        if (!stockform.isEmpty()) {
                            int intstockform = Integer.parseInt(stockform);
                            int qtyform = konst*coba.getWeeklySales() - intstockform;
                            if (qtyform >= 0) {
                                formQty.setHint(String.valueOf(qtyform));
                            } else {
                                formQty.setHint("0");
                            }
                        } else {
                            formQty.setHint("0");
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                final TextView finalFormKode = formKode;
                final int[] count = new int[1];
                dialog.setPositiveButton("Order", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mStock = formStock.getText().toString();
                        String mQty = formQty.getText().toString();
                        count[0] = 0;
                        if (mStock.isEmpty() && mQty.isEmpty()){
                            Toast.makeText(getContext(), "Mohon jangan kosongkan stock dan quantity order", Toast.LENGTH_SHORT).show();
                        } else if (mStock.isEmpty() && !mQty.isEmpty()) {

                            orderedID.add(coba.getId());
                            orderedkode.add(finalFormKode.getText().toString());
                            orderedname.add(formNama.getText().toString());
                            orderedprice.add(formHarga.getText().toString());
                            orderedstock.add("0");
                            orderedqty.add(formQty.getText().toString());
                            orderedcategory.add(coba.getCategory());

                            kumpulanorder.setId(orderedID.get(count[0]));
                            kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                            kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                            kumpulanorder.setPrice(orderedprice.get(count[0]));
                            kumpulanorder.setStock(orderedstock.get(count[0]));
                            kumpulanorder.setQty(orderedqty.get(count[0]));
                            kumpulanorder.setCategory(orderedcategory.get(count[0]));

                            order.add(count[0], kumpulanorder);

                            count[0]++;

                            coba.setStock("0");
                            coba.setQty(formQty.getText().toString());

                            TextView stock = view.findViewById(R.id.stock_hist);
                            TextView qty = view.findViewById(R.id.qtyhist);

                            stock.setText(spacecrafts.get(position).getStock());
                            qty.setText(spacecrafts.get(position).getQty());

//                            Toast.makeText(getActivity(), "order:" + formNama.getText().toString() + "stockmo " + coba.getStock() + " qtymo " + coba.getQty(), Toast.LENGTH_LONG).show();


                            boolean check = false;
                            boolean add = true;

                            for (int x = 0; x < Global.kode.size(); x++) {
                                if (finalFormKode.getText().toString().equals(Global.kode.get(x))) {
                                    check = true;
                                }
                                if (check) {
                                    Global.id_produk.set(x, coba.getId());
                                    Global.kode.set(x, finalFormKode.getText().toString());
                                    Global.nama.set(x, formNama.getText().toString());
                                    Global.harga.set(x, formHarga.getText().toString());
                                    Global.stock.set(x, "0");
                                    Global.qty.set(x, formQty.getText().toString());
                                    Global.kategori.set(x, coba.getCategory());
                                    Global.sgtorder.set(x, "0");
                                    check = false;
                                    add = false;
                                }

                            }
                            if (add) {
                                Global.produk.add(Global.produkCount, kumpulanorder);
                                Global.id_produk.add(coba.getId());
                                Global.kode.add(finalFormKode.getText().toString());
                                Global.nama.add(formNama.getText().toString());
                                Global.harga.add(formHarga.getText().toString());
                                Global.stock.add("0");
                                Global.qty.add(formQty.getText().toString());
                                Global.kategori.add(coba.getCategory());
                                Global.sgtorder.add("0");
                                Global.produkCount++;
                            }

                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

                        } else if (!mStock.isEmpty() && mQty.isEmpty()) {

                            orderedID.add(coba.getId());
                            orderedkode.add(finalFormKode.getText().toString());
                            orderedname.add(formNama.getText().toString());
                            orderedprice.add(formHarga.getText().toString());
                            orderedstock.add(formStock.getText().toString());
                            String stockform = formStock.getText().toString();
                            int intstockform = Integer.parseInt(stockform);
                            int qtyform = konst*coba.getWeeklySales() - intstockform;
                            if (qtyform<0){qtyform = 0;}
                            orderedqty.add(String.valueOf(qtyform));
                            orderedcategory.add(coba.getCategory());

                            kumpulanorder.setId(orderedID.get(count[0]));
                            kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                            kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                            kumpulanorder.setPrice(orderedprice.get(count[0]));
                            kumpulanorder.setStock(orderedstock.get(count[0]));
                            kumpulanorder.setQty(orderedqty.get(count[0]));
                            kumpulanorder.setCategory(orderedcategory.get(count[0]));

                            order.add(count[0], kumpulanorder);

                            count[0]++;

                            coba.setStock(formStock.getText().toString());
                            coba.setQty(String.valueOf(qtyform));

                            TextView stock = view.findViewById(R.id.stock_hist);
                            TextView qty = view.findViewById(R.id.qtyhist);

                            stock.setText(spacecrafts.get(position).getStock());
                            qty.setText(spacecrafts.get(position).getQty());

//                            Toast.makeText(getActivity(), "stock " + coba.getStock() + " qty " + coba.getQty(), Toast.LENGTH_LONG).show();

                            boolean check = false;
                            boolean add = true;

                            for (int x = 0; x < Global.kode.size(); x++) {
                                if (finalFormKode.getText().toString().equals(Global.kode.get(x))) {
                                    check = true;
                                }
                                if (check) {
                                    Global.id_produk.set(x, coba.getId());
                                    Global.kode.set(x, finalFormKode.getText().toString());
                                    Global.nama.set(x, formNama.getText().toString());
                                    Global.harga.set(x, formHarga.getText().toString());
                                    Global.stock.set(x, formStock.getText().toString());
                                    Global.qty.set(x, String.valueOf(qtyform));
                                    Global.kategori.set(x, coba.getCategory());
                                    Global.sgtorder.set(x, String.valueOf(qtyform));
                                    check = false;
                                    add = false;
                                }

                            }
                            if (add) {
                                Global.id_produk.add(coba.getId());
                                Global.produk.add(Global.produkCount, kumpulanorder);
                                Global.kode.add(finalFormKode.getText().toString());
                                Global.nama.add(formNama.getText().toString());
                                Global.harga.add(formHarga.getText().toString());
                                Global.stock.add(formStock.getText().toString());
                                Global.qty.add(String.valueOf(qtyform));
                                Global.kategori.add(coba.getCategory());
                                Global.sgtorder.add(String.valueOf(qtyform));
                                Global.produkCount++;
                            }


                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

                        } else {
                            String stockform = formStock.getText().toString();
                            int intstockform = Integer.parseInt(stockform);
                            int qtyform = konst*coba.getWeeklySales() - intstockform;
                            if (qtyform<0){qtyform = 0;}

                            orderedID.add(coba.getId());
                            orderedkode.add(finalFormKode.getText().toString());
                            orderedname.add(formNama.getText().toString());
                            orderedprice.add(formHarga.getText().toString());
                            orderedstock.add(formStock.getText().toString());
                            orderedqty.add(formQty.getText().toString());
                            orderedcategory.add(coba.getCategory());

                            kumpulanorder.setId(orderedID.get(count[0]));
                            kumpulanorder.setKodeodoo(orderedkode.get(count[0]));
                            kumpulanorder.setNamaproduk(orderedname.get(count[0]));
                            kumpulanorder.setPrice(orderedprice.get(count[0]));
                            kumpulanorder.setStock(orderedstock.get(count[0]));
                            kumpulanorder.setQty(orderedqty.get(count[0]));
                            kumpulanorder.setCategory(orderedcategory.get(count[0]));

                            order.add(count[0], kumpulanorder);

                            count[0]++;

                            coba.setStock(formStock.getText().toString());
                            coba.setQty(formQty.getText().toString());

                            TextView stock = view.findViewById(R.id.stock_hist);
                            TextView qty = view.findViewById(R.id.qtyhist);

                            stock.setText(spacecrafts.get(position).getStock());
                            qty.setText(spacecrafts.get(position).getQty());

//                            Toast.makeText(getActivity(), "order:" + formNama.getText().toString() + "stockmo " + coba.getStock() + " qtymo " + coba.getQty(), Toast.LENGTH_LONG).show();


                            boolean check = false;
                            boolean add = true;

                            for (int x = 0; x < Global.kode.size(); x++) {
                                if (finalFormKode.getText().toString().equals(Global.kode.get(x))) {
                                    check = true;
                                }
                                if (check) {
                                    Global.id_produk.set(x, coba.getId());
                                    Global.kode.set(x, finalFormKode.getText().toString());
                                    Global.nama.set(x, formNama.getText().toString());
                                    Global.harga.set(x, formHarga.getText().toString());
                                    Global.stock.set(x, formStock.getText().toString());
                                    Global.qty.set(x, formQty.getText().toString());
                                    Global.kategori.set(x, coba.getCategory());
                                    Global.sgtorder.set(x, String.valueOf(qtyform));
                                    check = false;
                                    add = false;
                                }

                            }
                            if (add) {
                                Global.produk.add(Global.produkCount, kumpulanorder);
                                Global.id_produk.add(coba.getId());
                                Global.kode.add(finalFormKode.getText().toString());
                                Global.nama.add(formNama.getText().toString());
                                Global.harga.add(formHarga.getText().toString());
                                Global.stock.add(formStock.getText().toString());
                                Global.qty.add(formQty.getText().toString());
                                Global.kategori.add(coba.getCategory());
                                Global.sgtorder.add(String.valueOf(qtyform));
                                Global.produkCount++;
                            }

                            adapter.notifyDataSetChanged();
                            dialog.dismiss();

                        }


                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        Button orderbutton = view.findViewById(R.id.order_buttonNPDWardah);
        orderbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putIntegerArrayList("ID", orderedID);
                bundle.putStringArrayList("kodemo", orderedkode);
                bundle.putStringArrayList("namamo", orderedname);
                bundle.putStringArrayList("hargamo", orderedprice);
                bundle.putStringArrayList("stok", orderedstock);
                bundle.putStringArrayList("kuantitas", orderedqty);
                Intent intent = new Intent(getActivity().getBaseContext(), com.example.sfmtesting.RingkasanWardahActivity.class);
                intent.putExtra("listorder", bundle);
                startActivity(intent);
            }
        });


        return view;
    }

    /*
     Our data object
     */
    static class FilterHelper extends Filter implements Serializable {
        ArrayList<com.example.sfmtesting.Spacecraft> currentList;
        ListViewAdapter adapter;
        Context c;

        public FilterHelper(ArrayList<com.example.sfmtesting.Spacecraft> currentList, ListViewAdapter adapter, Context c) {
            this.currentList = currentList;
            this.adapter = adapter;
            this.c = c;
        }

        /*-
        - Perform actual filtering.
        */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
//CHANGE TO UPPER
                constraint = constraint.toString().toUpperCase();
//HOLD FILTERS WE FIND
                ArrayList<com.example.sfmtesting.Spacecraft> foundFilters = new ArrayList<>();
                com.example.sfmtesting.Spacecraft spacecraft = null;
//ITERATE CURRENT LIST
                for (int i = 0; i < currentList.size(); i++) {
                    spacecraft = currentList.get(i);
//SEARCH
                    if (spacecraft.getKodeodoo().toUpperCase().contains(constraint) ||
                            spacecraft.getNamaproduk().toUpperCase().contains(constraint) ||
                            spacecraft.getBarcode().toUpperCase().contains(constraint))  {
                        //ADD IF FOUND
                        foundFilters.add(spacecraft);
                    }
                }
//SET RESULTS TO FILTER LIST
                filterResults.count = foundFilters.size();
                filterResults.values = foundFilters;
            } else {
//NO ITEM FOUND.LIST REMAINS INTACT
                filterResults.count = currentList.size();
                filterResults.values = currentList;
            }
//RETURN RESULTS
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            adapter.setSpacecrafts((ArrayList<com.example.sfmtesting.Spacecraft>) filterResults.values);
            adapter.refresh();
        }
    }

//    static class ViewHolder implements Serializable {
//        TextView product_odoo;
//        TextView product_name;
//        TextView product_price;
//        TextView product_stock;
//        TextView product_qty;
//    }

    /*
    Our custom adapter class
    */
    public class ListViewAdapter extends BaseAdapter implements Filterable, Serializable {
        public ArrayList<com.example.sfmtesting.Spacecraft> currentList;
        Context c;
        ArrayList<com.example.sfmtesting.Spacecraft> spacecrafts;
        FilterHelper filterHelper;
        Dialog dialog;

        public ListViewAdapter(Context c, ArrayList<com.example.sfmtesting.Spacecraft> spacecrafts) {
            this.c = c;
            this.spacecrafts = spacecrafts;
            this.currentList = spacecrafts;
        }

        @Override
        public int getCount() {
            return spacecrafts.size();
        }

        @Override
        public Object getItem(int i) {
            return spacecrafts.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            holder = new ViewHolder();
            if (view == null) {
                view = LayoutInflater.from(c).inflate(R.layout.model_row_hist, viewGroup, false);
                holder.cardView = (CardView) view.findViewById(R.id.cardview);
                holder.product_odoo = (TextView) view.findViewById(R.id.odoo_hist);
                holder.product_name = (TextView) view.findViewById(R.id.nama_hist);
                holder.product_price = (TextView) view.findViewById(R.id.harga_hist);
                holder.product_ws = (TextView) view.findViewById(R.id.order_BA_hist);
                holder.product_stock = (TextView) view.findViewById(R.id.stock_hist);
                holder.product_qty = (TextView) view.findViewById(R.id.qtyhist);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
                holder.product_odoo.setText("");
                holder.product_name.setText("");
                holder.product_price.setText("");
                holder.product_ws.setText("");
                holder.product_stock.setText("");
                holder.product_qty.setText("");
            }
//            if ((i+1) % 6 == 4 || (i+1) % 6 == 5 ||(i+1) % 6 == 0)
            if (i % 2 == 0)
            {
                holder.cardView.setBackgroundColor(Color.rgb(240, 240, 240));
            } else {
                holder.cardView.setBackgroundColor(Color.rgb(255, 255, 255));
            }
            final com.example.sfmtesting.Spacecraft s = (com.example.sfmtesting.Spacecraft) this.getItem(i);
            holder.product_odoo.setText(s.getKodeodoo());
            holder.product_name.setText(s.getNamaproduk());
            holder.product_price.setText(s.getPrice());
            holder.product_ws.setText(""+s.getWeeklySales());
            holder.product_stock.setText(s.getStock());
            holder.product_qty.setText(s.getQty());


            Log.e("Product type", s.getProducttype());
            if (s.getProducttype().equals("npd")) {
                //NPD - merah
                holder.product_odoo.setTextColor(Color.rgb(219, 93, 93));
                holder.product_name.setTextColor(Color.rgb(219, 93, 93));
                holder.product_price.setTextColor(Color.rgb(219, 93, 93));
            } else if (s.getProducttype().equals("promo")) {
                //PROMO- biru
                holder.product_odoo.setTextColor(Color.rgb(58, 139, 207));
                holder.product_name.setTextColor(Color.rgb(58, 139, 207));
                holder.product_price.setTextColor(Color.rgb(58, 139, 207));
            } else {
                holder.product_odoo.setTextColor(Color.BLACK);
                holder.product_name.setTextColor(Color.BLACK);
                holder.product_price.setTextColor(Color.BLACK);
            }

            return view;
        }

        public void setSpacecrafts(ArrayList<com.example.sfmtesting.Spacecraft> filteredSpacecrafts) {
            this.spacecrafts = filteredSpacecrafts;
        }

        @Override
        public Filter getFilter() {
            if (filterHelper == null) {
                filterHelper = new FilterHelper(currentList, this, c);
            }
            return filterHelper;
        }

        public void refresh() {
            notifyDataSetChanged();
        }
    }

    public class JSONDownloader implements Serializable {

        private final Context c;

        public JSONDownloader(Context c) {
            this.c = c;
        }

        /*
        Fetch JSON Data
        */
        public ArrayList<com.example.sfmtesting.Spacecraft> retrieve(final ListView mLpositiveistView, final ProgressBar myProgressBar) {
            final ArrayList<com.example.sfmtesting.Spacecraft> downloadedData = new ArrayList<>();
            final DatabaseNPDPromoHandler dbEBP = new DatabaseNPDPromoHandler(getContext());

            myProgressBar.setIndeterminate(true);
            myProgressBar.setVisibility(View.VISIBLE);
            pref = getActivity().getSharedPreferences("TokoPref", 0);
            editor = pref.edit();
            final String partnerid = pref.getString("partner_id", "0");
            Log.e("partnerid", partnerid);
            final ArrayList<com.example.sfmtesting.Spacecraft> listEBP = dbEBP.getAllProdukToko(partnerid, "brand:Wardah");
            for (int i=0; i<listEBP.size(); i++){
//                sc = dbEBP.getProduk(i);
//                if (sc != null && sc.getBrand().contains("Wardah") && sc.getPartner_id().equals(partnerid)){
//                    listEBP.add(sc);
////                    Log.e("LIST NPD", listEBP.get(i).getKodeodoo() + " - " +listEBP.get(i).getNamaproduk() + " - " +listEBP.get(i).getBrand()+ " - " +listEBP.get(i).getPartner_id());
//
//                }
////                listEBP.add(dbEBP.getProdukToko(i, partnerid, "brand:Wardah"));
                Log.e("LIST NPD", listEBP.get(i).getKodeodoo() + " - " +listEBP.get(i).getNamaproduk() + " - " +listEBP.get(i).getBrand()+ " - " +listEBP.get(i).getPartner_id());
            }
            Log.e("SIZE LIST NPD", ""+listEBP.size());
            final String customer = pref.getString("ref", "");
//            String barcode = pref.getString("barcode", "");

//            try {
//                Thread.sleep(400);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            String url = "https://sfa-api.pti-cosmetics.com/v_product_npd_promo?brand=ilike.*wardah&partner_ref=ilike.*" + customer;
            Log.e("url", url);
            AndroidNetworking.get(url)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            JSONObject jo;
                            com.example.sfmtesting.Spacecraft s;
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    jo = response.getJSONObject(i);
                                    int id = jo.getInt("product_id");
                                    String name = jo.getString("default_code");
                                    String propellant = jo.getString("name");
                                    String price = jo.getString("price");
                                    String cat = jo.getString("category");
                                    String barcode = jo.getString("barcode");
                                    String producttype = jo.getString("product_type");
                                    String unit = jo.getString("unit");
                                    int weekly = jo.getInt("weekly_qty");
                                    String stock = jo.getString("stock_qty");
//                                    String imageURL=jo.getString("imageurl");
                                    s = new com.example.sfmtesting.Spacecraft();
                                    s.setId(id);
                                    s.setKoli(unit);
                                    s.setBarcode(barcode);
                                    s.setKodeodoo(name);
                                    s.setNamaproduk(propellant);
                                    s.setWeeklySales(weekly);
                                    s.setPrice(price);
                                    s.setStock(stock);
                                    s.setQty("0");
                                    s.setCategory(cat);
                                    s.setProducttype(producttype);
//                                    s.setImageURL(imageURL);
//                                    s.setTechnologyExists(techExists.equalsIgnoreCase("1") ? 1 : 0);
                                    downloadedData.add(s);
                                }
                                myProgressBar.setVisibility(View.GONE);
                            } catch (JSONException e) {
                                myProgressBar.setVisibility(View.GONE);
//                                Toast.makeText(c, "GOOD RESPONSE BUT JAVA CAN'T PARSE JSON IT RECEIEVED. " + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("CANT PARSE JSON", e.getMessage());
                                com.example.sfmtesting.Spacecraft EBP;
                                for (com.example.sfmtesting.Spacecraft produk : listEBP){
                                    Log.e("ID", ""+produk.getId()+", Kode: "+produk.getKodeodoo()+", ");
//                                    if ((produk.getBrand().equals("brand:Emina")) && (produk.getPartner_id().equals(partnerid))){

                                    Log.e("NPD OFFLINE", "EMINA");
                                    EBP = new com.example.sfmtesting.Spacecraft();
                                    EBP.setId(produk.getId());
                                    EBP.setKoli(produk.getKoli());
                                    EBP.setKodeodoo(produk.getKodeodoo());
                                    EBP.setNamaproduk(produk.getNamaproduk());
                                    EBP.setCategory(produk.getCategory());
                                    EBP.setPrice(produk.getPrice());
                                    EBP.setProducttype(produk.getProducttype());
                                    EBP.setBarcode(produk.getBarcode());
                                    EBP.setWeeklySales(produk.getWeeklySales());
                                    EBP.setStock(produk.getStock());
                                    EBP.setQty(produk.getQty());
                                    downloadedData.add(EBP);

//                                    if (String.valueOf(produk.getPartner_id()) == partnerid){
//                                        downloadedData.add(EBP);
//                                        Log.e("DATA OFFLINE", "ADDED");
//                                    }

//                                    } else {
//                                        Log.e("NPD OFFLINE", "NOT ADDED");
//                                    }
                                }
                            }
                        }

                        //ERROR
                        @Override
                        public void onError(ANError anError) {
                            anError.printStackTrace();
                            myProgressBar.setVisibility(View.GONE);
                            Toast.makeText(c, "UNSUCCESSFUL :  ERROR IS : " + anError.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Error", anError.getMessage());
                            com.example.sfmtesting.Spacecraft EBP;
                            for (com.example.sfmtesting.Spacecraft produk : listEBP){
                                Log.e("ID", ""+produk.getId()+", Kode: "+produk.getKodeodoo()+", ");
//                                if ((produk.getBrand().equals("brand:Emina")) && (produk.getPartner_id().equals(partnerid))){

                                Log.e("NPD OFFLINE", "EMINA");
                                EBP = new com.example.sfmtesting.Spacecraft();
                                EBP.setId(produk.getId());
                                EBP.setKoli(produk.getKoli());
                                EBP.setKodeodoo(produk.getKodeodoo());
                                EBP.setNamaproduk(produk.getNamaproduk());
                                EBP.setCategory(produk.getCategory());
                                EBP.setPrice(produk.getPrice());
                                EBP.setProducttype(produk.getProducttype());
                                EBP.setBarcode(produk.getBarcode());
                                EBP.setWeeklySales(produk.getWeeklySales());
                                EBP.setStock(produk.getStock());
                                EBP.setQty(produk.getQty());
                                downloadedData.add(EBP);

//                                    if (String.valueOf(produk.getPartner_id()) == partnerid){
//                                        downloadedData.add(EBP);
//                                        Log.e("DATA OFFLINE", "ADDED");
//                                    }

//                                } else {
//                                    Log.e("NPD OFFLINE", "NOT ADDED");
//                                }
                            }
                        }
                    });
            return downloadedData;
        }
    }

}
