import express from "express";
import { crearInventario, listarInventario, actualizarPrecio, actualizarInventario } from "../controllers/inventario.controller.js";
import { verificarToken, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

router.put("/:id_inventario/precio", verificarToken, esAdmin, actualizarPrecio);
router.post("/", verificarToken, esAdmin, crearInventario);
router.get("/", verificarToken, listarInventario);
router.put("/:id_inventario", verificarToken, esAdmin, actualizarInventario);

export default router;
