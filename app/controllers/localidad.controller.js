// app/controllers/localidad.controller.js
import { Localidad, Inventario } from "../models/index.models.js";

export const crearLocalidad = async (req, res) => {
  try {
    const nueva = await Localidad.create(req.body);
    res.json(nueva);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al crear localidad" });
  }
};

export const listarLocalidades = async (req, res) => {
  try {
    const localidades = await Localidad.findAll();
    res.json(localidades);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al listar localidades" });
  }
};

// Actualizar precios de localidades
export const actualizarPrecios = async (req, res) => {
  try {
    const { precios } = req.body; // [{id_localidad: 1, precio: 100}, ...]
    
    const resultados = [];
    
    for (const item of precios) {
      const localidad = await Localidad.findByPk(item.id_localidad);
      if (localidad) {
        await localidad.update({ precio: item.precio });
        resultados.push({
          id: localidad.id_localidad,
          nombre: localidad.nombre,
          precio: item.precio,
          estado: 'actualizado'
        });
      } else {
        resultados.push({
          id: item.id_localidad,
          estado: 'no_encontrado'
        });
      }
    }
    
    res.json({ 
      mensaje: 'Precios actualizados correctamente',
      resultados 
    });
  } catch (error) {
    res.status(500).json({ mensaje: "Error al actualizar precios", error: error.message });
  }
};

//Actualizar precio de una localidad específica
export const actualizarPrecioLocalidad = async (req, res) => {
  try {
    const { id } = req.params;
    const { precio } = req.body;
    
    const localidad = await Localidad.findByPk(id);
    if (!localidad) {
      return res.status(404).json({ mensaje: "Localidad no encontrada" });
    }
    
    await localidad.update({ precio });
    res.json({ 
      mensaje: 'Precio actualizado correctamente',
      localidad 
    });
  } catch (error) {
    res.status(500).json({ mensaje: "Error al actualizar precio", error: error.message });
  }
};

// Listar localidades con inventario
export const listarLocalidadesConInventario = async (req, res) => {
  try {
    const { id_partido } = req.query;
    
    if (!id_partido) {
      return res.status(400).json({ mensaje: "ID del partido es requerido" });
    }

    const localidades = await Localidad.findAll({
      include: [
        {
          model: Inventario,
          attributes: ['cantidad_disponible'],
          where: { id_partido: id_partido },
          required: false
        }
      ]
    });

    const localidadesFormateadas = localidades.map(localidad => ({
      id_localidad: localidad.id_localidad,
      nombre: localidad.nombre,
      precio: localidad.precio,
      disponibilidad: localidad.Inventarios?.[0]?.cantidad_disponible || 0 // ← Inventarios con I mayúscula
    }));

    res.json(localidadesFormateadas);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al listar localidades", error: err.message });
  }
};