# 🍖 JOURNEYMAN PITCRAFT: POPUP PROTOCOL

## 📅 FRIDAY: THE IGNITION (Prep Day)
**Objective:** Establish Command Center & Network Tunnel.

1. **Start the Engine:**
   - Open Terminal.
   - Run: `./launch_pitcraft.sh`
   - Verify: Go to `http://localhost:3000` on Desktop.

2. **Open the Tunnel (For SMS):**
   - Run: `ngrok http 8080`
   - **CRITICAL:** Copy the `https://....ngrok-free.app` URL.

3. **Configure Twilio:**
   - Log into Twilio Console.
   - Paste the Ngrok URL into "Messaging > Webhook".
   - *Test:* Send "STATUS" from your cell to the BBQ Bot.

4. **Network Check (Mobile):**
   - Find Desktop IP (Mac: System Settings > Network).
   - Open phone browser: `http://<DESKTOP_IP>:3000`
   - *Success:* Dashboard loads on phone via Wi-Fi.

---

## 📅 SUNDAY: THE SHUTDOWN (Post-Service)
**Objective:** Secure Data & Power Down.

1. **The "Black Box" Save:**
   - Run: `./archive_cook.sh`
   - Name the file (e.g., "July4th_PopUp").

2. **Kill Switch:**
   - Run: `docker-compose down`

3. **Hardware:**
   - Close laptop/computer.
   - Go to sleep.
