/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iservport.auth.controller;

import org.helianto.core.repository.IdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para exibir o formulário de entrada.
 * 
 * @author mauriciofernandesdecastro
 */
@Controller
@RequestMapping("/login")
public class SecurityController {
	

	@Autowired 
	private IdentityRepository identityRepository;
	
	/**
	 * Exibe o formulário como HTML no web browser.
	 */
	@RequestMapping(value={"/", ""}, method=RequestMethod.GET)
	public String signin( String error, Model model, @RequestParam(required = false) String logout ) {
	
		System.err.println(error);
		if (error!=null && error.equals("1")) {
			model.addAttribute("error", "1");
		}
		model.addAttribute("baseName", "security");
		return "security/login";
	}
	
	/**
	 * Trata erros de login.
	 * 
	 * @param model
	 * @param username
	 */
	@RequestMapping(value="/error", method=RequestMethod.GET)
	public String loginError( Model model, @RequestParam String type) {
		model.addAttribute("loginFailMsg","label.user.error."+type);
		model.addAttribute("error",true);		
		model.addAttribute("baseName", "security");
		return "security/login";
	}
	
	
}
