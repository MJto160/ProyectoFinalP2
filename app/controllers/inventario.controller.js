import { Inventario, Partido, Localidad } from "../models/index.models.js";

// Crear inventario (por partido y localidad)
export const crearInventario = async (req, res) => {
  try {
    
    const { id_partido, id_localidad, cantidad_total, cantidad_disponible, precio } = req.body;

    const nuevo = await Inventario.create({
      id_partido,
      id_localidad,
      cantidad_total,
      cantidad_disponible,
      precio 
    });

    res.json(nuevo);
  } catch (err) {
    console.error(err);
    res.status(500).json({ mensaje: "Error al crear inventario" });
  }
};

// Listar inventarios con detalles de partido y localidad
export const listarInventario = async (req, res) => {
  try {
    const inventario = await Inventario.findAll({
      include: [Partido, Localidad],
    });
    res.json(inventario);
  } catch (err) {
    console.error(err);
    res.status(500).json({ mensaje: "Error al listar inventario" });
  }
};


// Actualizar precio de una localidad en un partido
export const actualizarPrecio = async (req, res) => {
  try {
    const { id_inventario } = req.params;
    const { precio } = req.body;

    const inventario = await Inventario.findByPk(id_inventario);
    if (!inventario) {
      return res.status(404).json({ mensaje: "Inventario no encontrado" });
    }

    inventario.precio = precio;
    await inventario.save();

    res.json({ mensaje: "Precio actualizado correctamente", inventario });
  } catch (err) {
    console.error(err);
    res.status(500).json({ mensaje: "Error al actualizar precio" });
  }
};


// Actualizar cualquier campo del inventario (precio, localidad, cantidad, etc.)
export const actualizarInventario = async (req, res) => {
  try {
    const { id_inventario } = req.params;
    const datos = req.body;

    const inventario = await Inventario.findByPk(id_inventario);
    if (!inventario) {
      return res.status(404).json({ mensaje: "Inventario no encontrado" });
    }

    await inventario.update(datos);
    res.json({ mensaje: "Inventario actualizado correctamente", inventario });
  } catch (err) {
    console.error(err);
    res.status(500).json({ mensaje: "Error al actualizar inventario" });
  }
};
