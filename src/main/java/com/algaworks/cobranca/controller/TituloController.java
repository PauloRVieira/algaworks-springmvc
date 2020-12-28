package com.algaworks.cobranca.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestAttributeMethodArgumentResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.cobranca.model.StatusTitulo;
import com.algaworks.cobranca.model.Titulo;
import com.algaworks.cobranca.repository.Titulos;
import com.algaworks.cobranca.repository.filter.TituloFilter;
import com.algaworks.cobranca.service.CadastroTituloService;

@Controller
@RequestMapping("/titulos")
public class TituloController {

	private static final String CADASTRO_VIEW = "CadastroTitulo"; 
	
	@Autowired
	private CadastroTituloService cadastroTituloService;
	
	@RequestMapping("/novo")
	public ModelAndView novo() {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
		mv.addObject(new Titulo());
		return mv;
	}
	
	@RequestMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Titulo titulo) {
		ModelAndView mv = new ModelAndView(CADASTRO_VIEW);
//		Titulo titulo = titulos.getOne(tituloId);
		
		mv.addObject("titulo", titulo);
		
		return mv;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String salvar(@Validated Titulo titulo, Errors errors, RedirectAttributes redirectAttributes) {
		if (errors.hasErrors()) {
			return CADASTRO_VIEW;
		}
		
		try {
			cadastroTituloService.salvar(titulo);
			redirectAttributes.addFlashAttribute("mensagem", "Título salvo com sucesso!");
			return "redirect:/titulos/novo";
		} catch (IllegalArgumentException e) {
			//errors.rejectValue("dataVencimento", null, "Formato de data inválido");
			redirectAttributes.addFlashAttribute("mensagem", e.getLocalizedMessage());
//			return CADASTRO_VIEW;
			return "redirect:/titulos/novo";
		}
		
		
	}
	
	@RequestMapping(value = "/{codigo}", method = RequestMethod.POST)
	public String excluir(@PathVariable Long codigo, RedirectAttributes redirectAttributes) {
		cadastroTituloService.excluir(codigo);
		
		redirectAttributes.addFlashAttribute("mensagem", "Título excluído com sucesso");
		
		return "redirect:/titulos";
	}
	
	// RequestMapping tem o default RequestMethod.GET
	@RequestMapping
	//public ModelAndView pesquisar(@RequestParam(defaultValue = "") String descricao) {
	public ModelAndView pesquisar(@ModelAttribute("filtro") TituloFilter filtro) {
		List<Titulo> todosTitulos = cadastroTituloService.filtrar(filtro);
		
		ModelAndView mv = new ModelAndView("PesquisaTitulos");
		mv.addObject("titulos", todosTitulos);
		
		return mv;
	}
	
	@RequestMapping(value = "/{codigo}/receber", method = RequestMethod.PUT)
	public @ResponseBody String receber(@PathVariable Long codigo) {
		return cadastroTituloService.receber(codigo);
	}

	@ModelAttribute("todosStatusTitulo")
	public List<StatusTitulo> getStatusTitulo() {
		return Arrays.asList(StatusTitulo.values());
	}
	
}
