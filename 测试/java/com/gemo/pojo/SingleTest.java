package com.gemo.pojo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gemo.annotation.Column;
import com.gemo.annotation.Dimension;
import com.gemo.annotation.Entity;
import com.gemo.annotation.Index;
import com.gemo.annotation.Property;
import com.gemo.annotation.Table;
import com.gemo.constant.SchemaConstant.Schema;
import com.gemo.mvc.pojo.BaseId;

@Dimension
@Entity(name = "test")
@Table(name = "z_test", comment = "测试", schema = Schema.UDM, indexes = { @Index(columns = { "c_name" }),
		@Index(columns = { "c_age", "c_height" }) })
public class SingleTest extends BaseId {

	private static final long serialVersionUID = 1303834707464290110L;

	@Column(order = 1, name = "c_name", length = 50, nullable = false, comment = "名称")
	@JsonProperty("name")
	private String name;

	@Column(order = 2, name = "c_age", length = 3, nullable = false, comment = "年龄")
	@JsonProperty("age")
	private Integer age;

	@Column(order = 3, name = "c_height", length = 3, scale = 2, nullable = false, comment = "身高")
	@JsonProperty("height")
	private Double height;

	@Column(order = 4, name = "c_male", length = 1, nullable = false, comment = "男/女")
	@JsonProperty("male")
	private Boolean male;

	@Column(order = 5, name = "c_birthday", length = 0, nullable = false, comment = "生日")
	@JsonProperty("birthday")
	private Date birthday;

	@Column(order = 6, name = "c_remark", length = Integer.MAX_VALUE, comment = "备注")
	@JsonProperty("remark")
	private String remark;

	@Property
	@JsonProperty("property")
	private List<SingleTestProperty> propertyList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public Boolean getMale() {
		return male;
	}

	public void setMale(Boolean male) {
		this.male = male;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<SingleTestProperty> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<SingleTestProperty> propertyList) {
		this.propertyList = propertyList;
	}

}
