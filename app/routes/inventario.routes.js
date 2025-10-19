import express from "express";
import { crearInventario, listarInventario } from "../controllers/inventario.controller.js";
import { verificarToken, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

router.post("/", verificarToken, esAdmin, crearInventario);
router.get("/", verificarToken, esAdmin, listarInventario);

export default router;
