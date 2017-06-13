package shahar.socademic;

/**
 * Model: data only
 *
 * there is nothing related to view here. (and it shouldn't, because we keep
 * the model in memory longer than a lifecycle of an Activity).
 *
 * Created by amir on 11/29/16.
 */
class gItem {
    private String label;

    public gItem(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
