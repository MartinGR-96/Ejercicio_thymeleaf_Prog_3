package com.progr3.tarea.controladores;

import com.progr3.tarea.entidades.Videojuego;
import com.progr3.tarea.servicios.ServicioCategoria;
import com.progr3.tarea.servicios.ServicioEstudio;
import com.progr3.tarea.servicios.ServicioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

@Controller
public class controladorVideojuego {
    @Autowired
    private ServicioVideojuego svcVideoJuego;

    @Autowired
    private ServicioEstudio svcEstudio;

    @Autowired
    private ServicioCategoria svcCategoria;

    @GetMapping("/")
    public String index(Model model){
        try{
            String cabecera = "Diseño de Verna";
            model.addAttribute("cabecera",cabecera);
            return "index";
        }catch (Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/inicio")
    public String inicio(Model model) {
        try {
            List<Videojuego> videojuegos = this.svcVideoJuego.findAllByActivo();
            model.addAttribute("videojuegos", videojuegos);

            return "views/inicio";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/detalle/{id}")
    public String detalleVideojuego(Model model, @PathVariable("id") long id){
        try{
            Videojuego videojuego = this.svcVideoJuego.findByIdAndActivo(id);
            model.addAttribute("videojuego",videojuego);
            return "views/detalle";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping(value= "/busqueda")
    public String busquedaVideojuego(Model model, @RequestParam(value="query",required=false) String q){
        try{
            List<Videojuego> videojuegos=this.svcVideoJuego.findByTitle(q);
            model.addAttribute("videojuegos",videojuegos);
            return "views/busqueda";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/crud")
    public String crudVideojuego(Model model){
        try{
            List<Videojuego> videojuegos = this.svcVideoJuego.findAll();
            model.addAttribute("videojuegos",videojuegos);
            return "views/crud";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/formulario/videojuego/{id}")
    public String formularioVideojuego(Model model, @PathVariable("id") long id){
        try{
            model.addAttribute("categorias",this.svcCategoria.findAll());
            model.addAttribute("estudios", this.svcEstudio.findAll());
            if(id==0){
                model.addAttribute("videojuegos",new Videojuego());
            }else{
                model.addAttribute("videojuegos",this.svcVideoJuego.findById(id));
            }
            return "views/formulario/videojuego";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/formulario/videojuego/{id}")
    public String guardarVideojuego(
            @RequestParam("archivo") MultipartFile archivo,
            @Valid @ModelAttribute("videojuegos") Videojuego videojuego,
            Model model,
            BindingResult result,
            @PathVariable("id") long id){
        try{
            model.addAttribute("categorias",this.svcCategoria.findAll());
            model.addAttribute("estudios",this.svcEstudio.findAll());
            if (result.hasErrors()){
                return "views/formulario/videojuego";
            }
            String ruta ="C://Videojuegos/imagenes";
            int index = archivo.getOriginalFilename().indexOf(".");
            String extension="";
            extension="."+archivo.getOriginalFilename().substring(index+1);
            String nombreFoto= Calendar.getInstance().getTimeInMillis()+extension;
            Path rutaAbsoluta = id !=0 ? Paths.get(ruta+"//"+videojuego.getImagen()):
                    Paths.get(ruta+"//"+nombreFoto);
            if(id==0){
                if(archivo.isEmpty()){
                    model.addAttribute("imageErrorMsg","La extension es requerida");
                    return "views/formulario/videojuego";
                }
                if(!this.validarExtension(archivo)){
                    model.addAttribute("imageErrorMsg","La extension no es válida");
                    return "views/formulario/videojuego";
                }
                if(archivo.getSize() >= 15000000){
                    model.addAttribute("imageErrorMsg","El peso excede lo permitido de 15 MB");
                }
                Files.write(rutaAbsoluta,archivo.getBytes());
                videojuego.setImagen(nombreFoto);
                this.svcVideoJuego.saveOne(videojuego);
            }else{
                if(!archivo.isEmpty()){
                    if(!this.validarExtension(archivo)){
                        model.addAttribute("imageErrorMsg","La extension no es válida");
                        return "views/formulario/videojuego";
                    }
                    if(archivo.getSize() >= 15000000){
                        model.addAttribute("imageErrorMsg","El peso excede lo permitido de 15 MB");
                    }
                    Files.write(rutaAbsoluta,archivo.getBytes());
                }
                this.svcVideoJuego.updateOne(videojuego,id);
            }
            return "redirect:/views/crud";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @GetMapping("/eliminar/videojuego/{id}")
    public String eliminarVideojuego(Model model, @PathVariable("id") long id){
        try{
            model.addAttribute("videojuegos",this.svcVideoJuego.findById(id));
            return "views/formulario/eliminar";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/eliminar/videojuego/{id}")
    public String desactivarVideojuego(Model model, @PathVariable("id") long id){
        try{
            this.svcVideoJuego.deleteById(id);
            return "redirect:/views/crud";
        }catch(Exception e){
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }

    public boolean validarExtension(MultipartFile archivo){
        try{
            ImageIO.read(archivo.getInputStream()).toString();
            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }
}
