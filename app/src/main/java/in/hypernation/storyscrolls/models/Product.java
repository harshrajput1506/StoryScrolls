package in.hypernation.storyscrolls.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

public class Product implements Parcelable {

    private String id;
    private String name;
    private String image;
    private String description;
    private long price;
    private long availableQuantity;
    private Timestamp addDateTime;
    private String category;


    public Product(String id, String name, String image, String description, long price, long availableQuantity, Timestamp addDateTime, String category) {
        this.name = name;
        this.id = id;
        this.image = image;
        this.description = description;
        this.price = price;
        this.availableQuantity = availableQuantity;
        this.addDateTime = addDateTime;
        this.category = category;
    }

    protected Product(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        image = in.readString();
        price = in.readLong();
        availableQuantity = in.readLong();
        addDateTime = in.readParcelable(Timestamp.class.getClassLoader());
        category = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPrice() {
        return price;
    }

    public long getAvailableQuantity() {
        return availableQuantity;
    }

    public Timestamp getAddDateTime() {
        return addDateTime;
    }

    public void setAddDateTime(Timestamp addDateTime) {
        this.addDateTime = addDateTime;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setAvailableQuantity(long availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeLong(price);
        dest.writeLong(availableQuantity);
        dest.writeParcelable(addDateTime, flags);
        dest.writeString(category);
    }
}
