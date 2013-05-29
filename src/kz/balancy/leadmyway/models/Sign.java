package kz.balancy.leadmyway.models;

import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Select;

@Table(name = "Signs")
public class Sign extends Model {

	@Column(name = "Name")
	public String name;

	@Column(name = "Sign")
	public byte[] signCascade;

	@Column(name = "Image")
	public byte[] image;

	@Column(name = "Category")
	public Category category;

	public Sign(String name, Category category, byte[] signCascade, byte[] image){
		this.name = name;
		this.category = category;
		this.signCascade = signCascade;
		this.image = image;
	}

	public static List<Sign> getAll(Category category) {
		return new Select()
		.from(Sign.class)
		.where("Category = ?", category.getId())
		.orderBy("name ASC")
		.execute();
	}

}
