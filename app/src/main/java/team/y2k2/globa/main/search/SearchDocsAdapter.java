package team.y2k2.globa.main.search;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import team.y2k2.globa.databinding.ItemDocsBinding;
import team.y2k2.globa.docs.DocsActivity;

import android.content.Intent;

public class SearchDocsAdapter extends RecyclerView.Adapter<SearchDocsAdapter.ViewHolder> {

    private List<SearchDocsItem> items;

    public SearchDocsAdapter(List<SearchDocsItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDocsBinding binding = ItemDocsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchDocsItem currentItem = items.get(position);
        holder.bind(currentItem);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void setItems(List<SearchDocsItem> newItems) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new SearchDiffCallback(items, newItems));
        items = newItems;
        result.dispatchUpdatesTo(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemDocsBinding binding;

        public ViewHolder(ItemDocsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SearchDocsItem item) {
            binding.textviewDocumentTitle.setText(item.getTitle());
            binding.textviewDocumentDatetime.setText(item.getDatetime());

            binding.constraintlayoutDocumentItem.setOnClickListener(v -> {
                Intent intent = new Intent(binding.getRoot().getContext(), DocsActivity.class);
                intent.putExtra("title", item.getTitle());
                intent.putExtra("folderId", item.getFolderId());
                intent.putExtra("recordId", item.getRecordId());
                binding.getRoot().getContext().startActivity(intent);
            });
        }
    }

    private static class SearchDiffCallback extends DiffUtil.Callback {

        private final List<SearchDocsItem> oldList;
        private final List<SearchDocsItem> newList;

        public SearchDiffCallback(List<SearchDocsItem> oldList, List<SearchDocsItem> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            SearchDocsItem oldItem = oldList.get(oldItemPosition);
            SearchDocsItem newItem = newList.get(newItemPosition);
            return oldItem.getRecordId().equals(newItem.getRecordId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            SearchDocsItem oldItem = oldList.get(oldItemPosition);
            SearchDocsItem newItem = newList.get(newItemPosition);
            return oldItem.equals(newItem);
        }
    }
}