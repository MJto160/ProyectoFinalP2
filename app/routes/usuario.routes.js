import express from "express";
import { crearUsuario, listarUsuarios } from "../controllers/usuario.controller.js";
import { verificarToken, esAdmin } from "../middlewares/authJwt.js";

const router = express.Router();

router.post("/", verificarToken, esAdmin, crearUsuario);
router.get("/", verificarToken, esAdmin, listarUsuarios);

export default router;
