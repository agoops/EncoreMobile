package com.encore.API.models;

import com.google.gson.annotations.SerializedName;

public class Session {
	@SerializedName("title")
	private String title;
	
	@SerializedName("crowd_title")
	private String crowdTitle; 
	
	@SerializedName("crowd_id")
	private String crowdId;
	
	@SerializedName("use_existing_crowd")
	private boolean useExistingCrowd;
	
	@SerializedName("crowdMembers")
	private String[] crowdMembers;
	
	public Session() {
		this.title = null;
		this.crowdTitle = null;
		this.crowdId = null;
		this.useExistingCrowd = false;
		this.crowdMembers = null;
	}
	
	public Session(String title, boolean use_existing_crowd, String crowd_title, 
			String[] crowd_members, String crowd_id) {
		this.title = title;
		this.crowdTitle = crowd_title;
		this.crowdId = crowd_id;
		this.useExistingCrowd = use_existing_crowd;
		this.crowdMembers = crowd_members;
	}
}
