package model.roadmap;

import java.util.ArrayList;
import java.util.List;

import model.category.Category;

public class Roadmap {

	private String name;
	private boolean completed;
	private String description;
	private Achievement achievement;
	private List<Category> categories = new ArrayList<Category>();
	private List<Step> steps = new ArrayList<Step>();

	public Roadmap() {
	}

	public Roadmap(String nm, boolean com, String description, Achievement ach) {
		this.name = nm;
		this.completed = com;
		this.description = description;
		this.setAchievement(ach);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Achievement getAchievement() {
		return achievement;
	}

	public void setAchievement(Achievement achievement) {
		this.achievement = achievement;
	}

	public boolean addStep(Step s) {
		return steps.add(s);
	}

	public boolean removeStep(Step s) {
		return steps.remove(s);
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	public boolean addCategory(Category c) {
		return categories.add(c);
	}

	public boolean removeCategory(Category c) {
		return categories.remove(c);
	}

	@Override
	public String toString() {
		return "Roadmap [name=" + name + ", completed=" + completed + ", description=" + description + ", achievement="
				+ achievement + ", categories=" + categories + ", steps=" + steps + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((achievement == null) ? 0 : achievement.hashCode());
		result = prime * result + ((categories == null) ? 0 : categories.hashCode());
		result = prime * result + (completed ? 1231 : 1237);
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((steps == null) ? 0 : steps.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Roadmap other = (Roadmap) obj;
		if (achievement == null) {
			if (other.achievement != null)
				return false;
		} else if (!achievement.equals(other.achievement))
			return false;
		if (categories == null) {
			if (other.categories != null)
				return false;
		} else if (!categories.equals(other.categories))
			return false;
		if (completed != other.completed)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (steps == null) {
			if (other.steps != null)
				return false;
		} else if (!steps.equals(other.steps))
			return false;
		return true;
	}
}