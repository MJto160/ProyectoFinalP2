// app/controllers/venta.controller.js
import { sequelize, Venta, DetalleVenta, Inventario } from "../models/index.models.js";

//CREAR VENTA
export const crearVenta = async (req, res) => {
  const t = await sequelize.transaction();
  try {
    const user = req.user; // viene del token
    const { detalles } = req.body;

    // Solo vendedores pueden crear ventas
    if (user.rol !== "Vendedor" && user.rol !== "vendedor") {
      return res.status(403).json({ mensaje: "Solo los vendedores pueden registrar ventas" });
    }

    // Calculamos el total de la venta
    const total = detalles.reduce(
      (sum, d) => sum + d.cantidad * d.precio_unitario,
      0
    );

    // Creamos la venta asociada al vendedor logueado
    const venta = await Venta.create(
      { id_vendedor: user.id_usuario, total_venta: total },
      { transaction: t }
    );

    // Recorremos los detalles y actualizamos inventario
    for (const d of detalles) {
      const inv = await Inventario.findOne({
        where: { id_partido: d.id_partido, id_localidad: d.id_localidad },
      });

      if (!inv || inv.cantidad_disponible < d.cantidad) {
        throw new Error("Boletos insuficientes en inventario");
      }

      // Crear detalle de la venta
      await DetalleVenta.create(
        {
          id_venta: venta.id_venta,
          id_localidad: d.id_localidad,
          id_partido: d.id_partido,
          cantidad: d.cantidad,
          precio_unitario: d.precio_unitario,
        },
        { transaction: t }
      );

      // Actualizar inventario
      inv.cantidad_disponible -= d.cantidad;
      await inv.save({ transaction: t });
    }

    await t.commit();
    res.json({ mensaje: "✅ Venta registrada correctamente", venta });
  } catch (err) {
    await t.rollback();
    res.status(500).json({ mensaje: "Error al registrar la venta", error: err.message });
  }
};

// LISTAR TODAS LAS VENTAS 
export const listarVentas = async (req, res) => {
  try {
    const user = req.user;
    let where = {};

    // Si es vendedor, solo ve sus ventas
    if (user.rol === "Vendedor" || user.rol === "vendedor") {
      where = { id_vendedor: user.id_usuario };
    }

    const ventas = await Venta.findAll({
      where,
      include: [{ all: true }], // incluye DetalleVenta, y lo demas.
    });

    res.json(ventas);
  } catch (err) {
    res.status(500).json({ mensaje: "Error al obtener las ventas", error: err.message });
  }
};

//ELIMINAR VENTA (solo el admin)
export const eliminarVenta = async (req, res) => {
  try {
    const user = req.user;

    if (user.rol !== "Administrador" && user.rol !== "admin") {
      return res.status(403).json({ mensaje: "Solo administradores pueden eliminar ventas" });
    }

    const { id } = req.params;
    const venta = await Venta.findByPk(id);
    if (!venta) return res.status(404).json({ mensaje: "Venta no encontrada" });

    await venta.destroy();
    res.json({ mensaje: "Venta eliminada correctamente" });
  } catch (err) {
    res.status(500).json({ mensaje: "Error al eliminar la venta", error: err.message });
  }
};
