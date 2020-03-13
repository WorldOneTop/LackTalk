//package com.example.lacktalk;
//
//import android.widget.Filter;
//
//public class HelpFilter extends Filter {
//    private HelpFilter(){ }
//    private static class SingletonHolder {
//        public static final HelpFilter INSTANCE = new HelpFilter();
//    }
//    public static HelpFilter getInstance() {
//        return SingletonHolder.INSTANCE;
//    }
//
//    @Override
//    protected FilterResults performFiltering(CharSequence constraint) {
//        FilterResults results = new FilterResults() ;
//        if (constraint == null || constraint.length() == 0) {
//            results.values = listViewItemList ;
//            results.count = listViewItemList.size() ;
//        } else {
//            ArrayList<ListViewItem> itemList = new ArrayList<ListViewItem>() ;
//
//            for (ListViewItem item : listViewItemList) {
//                if (item.getTitle().toUpperCase().contains(constraint.toString().toUpperCase()) ||
//                        item.getDesc().toUpperCase().contains(constraint.toString().toUpperCase()))
//                {
//                    itemList.add(item) ;
//                }
//            }
//
//            results.values = itemList ;
//            results.count = itemList.size() ;
//        }
//        return results;
//    }
//
//    @Override
//    protected void publishResults(CharSequence constraint, FilterResults results) {
//
//        // update listview by filtered data list.
//        filteredItemList = (ArrayList<ListViewItem>) results.values ;
//
//        // notify
//        if (results.count > 0) {
//            notifyDataSetChanged() ;
//        } else {
//            notifyDataSetInvalidated() ;
//        }
//    }
//}
//}
