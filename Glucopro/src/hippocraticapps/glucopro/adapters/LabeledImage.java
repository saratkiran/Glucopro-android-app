package hippocraticapps.glucopro.adapters;

public class LabeledImage
{
    private int imageResource;
    private String label;
    private Class activity;


    public LabeledImage(String label, int imageResource, Class activity) {
        this.label = label;
        this.imageResource = imageResource;
        this.activity = activity;
    }



    public int getImageResource() {
        return imageResource;
    }



    public String getLabel() {
        return label;
    }



    public Class getActivity() {
        return activity;
    }
}
