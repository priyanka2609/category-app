package com.spring.api.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spring.api.entities.ApiResponse;
import com.spring.api.entities.ApiResponseObj;
import com.spring.api.entities.Category;
import com.spring.api.repositories.CategoryRepository;

@RestController
@RequestMapping("/categories")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	// add categories
	@PostMapping("/add")
	public ResponseEntity<ApiResponse> addSubcategory(@RequestBody Category category) {
		Category parentCategory = categoryRepository.findById(category.getPid());
		if (parentCategory == null) {
			Category savedCategory = categoryRepository.save(category);
			ApiResponse response = new ApiResponse(true,"Successfully Added");
			return ResponseEntity.ok(response);
		} else {
			Category subcategory = new Category(category.getPid(), category.getTitle());
			Category savedSubcategory = categoryRepository.save(subcategory);
			ApiResponse response = new ApiResponse(true,"Successfully Added");
			return ResponseEntity.ok(response);
		}
	}

	// get all subcategories
	@GetMapping("/subcategories/all")
	public ResponseEntity<ApiResponseObj> getAllSubcategories() {
		List<Category> categories = categoryRepository.findAll();
		List<Map<String, Object>> data = new ArrayList<>();
		for (Category category : categories) {
			if (category.getPid() == 0) { // only process parent categories
				Map<String, Object> categoryData = new HashMap<>();
				categoryData.put("id", category.getId());
				categoryData.put("title", category.getTitle());
				categoryData.put("child", getChildCategories(category.getId(), categories));
				data.add(categoryData);
			}
		}
		ApiResponseObj response = new ApiResponseObj(true,data);
		return ResponseEntity.ok(response);
	}

	private List<Map<String, Object>> getChildCategories(int parentId, List<Category> categories) {
		List<Map<String, Object>> childCategories = new ArrayList<>();
		for (Category category : categories) {
			if (category.getPid() == parentId) {
				Map<String, Object> categoryData = new HashMap<>();
				categoryData.put("id", category.getId());
				categoryData.put("title", category.getTitle());
				categoryData.put("child", getChildCategories(category.getId(), categories));
				childCategories.add(categoryData);
			}
		}
		return childCategories;
	}

	// update
	@PutMapping("/update")
	public ResponseEntity<ApiResponse> updateSubcategory(@RequestBody Category category) {
		Category existingCategory = categoryRepository.findById(category.getId());
		if (existingCategory == null) {
			ApiResponse response = new ApiResponse(false,"Category cannot be updated");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(response);
		}
		existingCategory.setTitle(category.getTitle());
		categoryRepository.save(existingCategory);
		ApiResponse response = new ApiResponse(true,"Successfully Updated");
		return ResponseEntity.ok(response);
	}

	// delete
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse> deleteSubcategory(@RequestBody Category category) {
		Category subcategory = categoryRepository.findById(category.getId());
		if (subcategory == null) {
			ApiResponse response = new ApiResponse(false,"Category cannot be updated");
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(response);
		}
		categoryRepository.delete(subcategory);
		ApiResponse response = new ApiResponse(true,"Successfully Deleted");
		return ResponseEntity.ok(response);
	}

}
