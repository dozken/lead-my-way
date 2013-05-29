package kz.balancy.leadmyway.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

@Table(name = "Signs")
public class Category extends Model {

	@Column(name = "Name")
	public String name;
	
	public Category(String name){
		this.name = name;
	}
	
	public Category getByName(String name){
		return new Select().from(Category.class).where("name = ?", name).executeSingle();
	}
}
