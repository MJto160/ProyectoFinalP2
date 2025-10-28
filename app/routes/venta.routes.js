// app/routes/venta.routes.js
import express from "express";
import {
  crearVenta,
  listarVentas,
  eliminarVenta,
} from "../controllers/venta.controller.js";
import { verificarToken, esVendedor, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

//  Solo vendedores pueden crear ventas
router.post("/", verificarToken, esVendedor, crearVenta);

//  Vendedores ven solo sus ventas, admin ve todas
router.get("/", verificarToken, listarVentas);

//  Solo admin puede eliminar ventas
router.delete("/:id", verificarToken, esAdmin, eliminarVenta);

export default router;
