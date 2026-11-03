"use client"
import { useState, useEffect } from "react"
import Link from "next/link"
import Image from "next/image"
import { usePathname } from "next/navigation"
import styles from "./navegation.module.css"
import {
    FaHome,
    FaUserPlus,
    FaBoxes,
    FaHandHoldingHeart,
    FaUsers,
    FaChartBar,
    FaCog,
    FaUser,
    FaQuestionCircle,
    FaShoppingCart,
    FaAngleDoubleLeft,
    FaAngleDoubleRight
} from "react-icons/fa"

const menuIcons = {
    Home: <FaHome />,
    Cadastro: <FaUserPlus />,
    Estoque: <FaBoxes />,
    Doadores: <FaHandHoldingHeart />,
    Beneficiários: <FaUsers />,
    Retirada: <FaShoppingCart />,
    Relatórios: <FaChartBar />,
    Configurações: <FaCog />,
    Usuários: <FaUser />,
    Ajuda: <FaQuestionCircle />
}

const STORAGE_KEY = "sanem_sidebar_condensed"

export default function Navigation() {
    const pathname = usePathname()
    const [isCondensed, setIsCondensed] = useState(false)

    const applySidebarDimensions = (condensed) => {
        if (typeof document !== "undefined") {
            document.documentElement.style.setProperty("--sidebar-width", condensed ? "76px" : "220px")
            document.documentElement.style.setProperty("--sidebar-offset", condensed ? "96px" : "250px")
        }
    }

    useEffect(() => {
        try {
            const saved = localStorage.getItem(STORAGE_KEY) === "true"
            setIsCondensed(saved)
            applySidebarDimensions(saved)
        } catch {
            applySidebarDimensions(false)
        }
    }, [])

    const toggleSidebar = () => {
        setIsCondensed((prev) => {
            const next = !prev
            try {
                localStorage.setItem(STORAGE_KEY, String(next))
            } catch {
                // ignore storage errors
            }
            applySidebarDimensions(next)
            return next
        })
    }

    const isActive = (path) => {
        if (path === "/home") {
            return pathname === "/home"
        }
        return pathname?.startsWith(path)
    }

    return (
        <aside className={`${styles.sidebar} ${isCondensed ? styles.condensed : ""}`}>
            <div className={styles.sidebarHeader}>
                <div className={styles.logoContainer}>
                    <Image
                        src="/logo-sanem.svg"
                        alt="Sanem"
                        width={isCondensed ? 42 : 76}
                        height={isCondensed ? 42 : 76}
                        className={styles.logoImage}
                    />
                    {!isCondensed && <div className={styles.logoText}></div>}
                </div>
                <button
                    type="button"
                    onClick={toggleSidebar}
                    className={styles.toggleBtn}
                    title={isCondensed ? "Expandir menu" : "Condensar menu"}
                    aria-label={isCondensed ? "Expandir menu" : "Condensar menu"}
                    aria-expanded={!isCondensed}
                >
                    {isCondensed ? <FaAngleDoubleRight /> : <FaAngleDoubleLeft />}
                </button>
            </div>

            <div className={styles.scrollArea}>
                <div className={styles.sectionTitle}>{!isCondensed ? "MENU" : ""}</div>
                <nav className={styles.menuSection}>
                    <Link
                        href="/home"
                        title="Home"
                        className={`${styles.menuItem} ${isActive("/home") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Home}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Home</span>}
                    </Link>
                    <Link
                        href="/estoque"
                        title="Estoque"
                        className={`${styles.menuItem} ${isActive("/estoque") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Estoque}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Estoque</span>}
                    </Link>
                    <Link
                        href="/doacao/lista"
                        title="Doações"
                        className={`${styles.menuItem} ${isActive("/doacao") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}><FaHandHoldingHeart /></span>
                        {!isCondensed && <span className={styles.menuLabel}>Doações</span>}
                    </Link>
                    <Link
                        href="/retirada"
                        title="Retirada"
                        className={`${styles.menuItem} ${isActive("/retirada") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Retirada}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Retirada</span>}
                    </Link>
                    <Link
                        href="/cadastrodoador/lista"
                        title="Doadores"
                        className={`${styles.menuItem} ${isActive("/cadastrodoador") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Doadores}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Doadores</span>}
                    </Link>
                    <Link
                        href="/cadastrobeneficiario/lista"
                        title="Beneficiários"
                        className={`${styles.menuItem} ${isActive("/cadastrobeneficiario") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Beneficiários}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Beneficiários</span>}
                    </Link>
                </nav>

                <div className={styles.sectionTitle}>{!isCondensed ? "OTHERS" : ""}</div>
                <nav className={styles.menuSection}>
                    <Link
                        href="/relatorios"
                        title="Relatórios"
                        className={`${styles.menuItem} ${isActive("/relatorios") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Relatórios}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Relatórios</span>}
                    </Link>
                    <Link
                        href="/configuracoes"
                        title="Configurações"
                        className={`${styles.menuItem} ${isActive("/configuracoes") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Configurações}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Configurações</span>}
                    </Link>
                    <Link
                        href="/usuarios"
                        title="Usuários"
                        className={`${styles.menuItem} ${isActive("/usuarios") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Usuários}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Usuários</span>}
                    </Link>
                    <Link
                        href="/ajuda"
                        title="Ajuda"
                        className={`${styles.menuItem} ${isActive("/ajuda") ? styles.active : ""}`}
                    >
                        <span className={styles.menuIcon}>{menuIcons.Ajuda}</span>
                        {!isCondensed && <span className={styles.menuLabel}>Ajuda</span>}
                    </Link>
                </nav>
            </div>

            <div className={styles.sidebarFooter}>
                <button
                    type="button"
                    onClick={toggleSidebar}
                    className={styles.collapseFooterBtn}
                    title={isCondensed ? "Expandir menu" : "Condensar menu"}
                >
                    <span className={styles.menuIcon}>
                        {isCondensed ? <FaAngleDoubleRight /> : <FaAngleDoubleLeft />}
                    </span>
                    {!isCondensed && <span className={styles.menuLabel}>Recolher menu</span>}
                </button>
            </div>
        </aside>
    )
}
