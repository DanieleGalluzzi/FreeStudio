/* =========================
   BURGER MENU MOBILE
========================= */
const menuToggle = document.getElementById("menuToggle");
const sidebar = document.getElementById("sidebar");

menuToggle.addEventListener("click", () => {
    sidebar.classList.toggle("aperta");
});

// Chiude la sidebar quando si clicca su un link (solo mobile)
document.querySelectorAll(".menu a").forEach(link => {
    link.addEventListener("click", () => {
        sidebar.classList.remove("aperta");
    });
});


/* =========================
   ANIMAZIONE A SCOMPARSA
   (SI RIPETE OGNI VOLTA)
========================= */
const sezioni = document.querySelectorAll(".sezione");

const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add("attiva");     
        } else {
            entry.target.classList.remove("attiva"); 
        }
    });
}, {
    threshold: 0.2
});

sezioni.forEach(sezione => observer.observe(sezione));


/* =========================
   BOTTONE TORNA SU
========================= */
const scrollTopBtn = document.getElementById("scrollTopBtn");

scrollTopBtn.addEventListener("click", () => {
    window.scrollTo({
        top: 0,
        behavior: "smooth"
    });
});
