import { useEffect, useState } from 'react'
import axios from 'axios'
import { Flame, Server } from 'lucide-react'

function App() {
  const [status, setStatus] = useState<string>('CONNECTING TO NEXUS...')

  useEffect(() => {
    // Check Backend Connection
    axios.get('/api/system/status')
      .then(res => setStatus(`ONLINE: ${res.data.javaVersion}`))
      .catch(() => setStatus('NEXUS OFFLINE'))
  }, [])

  return (
    <div className="min-h-screen bg-slate-900 text-slate-100 font-mono p-8">
      <header className="border-b-4 border-orange-600 pb-4 mb-8">
        <h1 className="text-4xl font-bold flex items-center gap-3">
          <Flame className="text-orange-500 w-10 h-10" />
          JOURNEYMAN NEXUS
          <span className="text-slate-400 font-normal">PITCRAFT</span>
        </h1>
        <p className="text-slate-400 mt-2">Precision BBQ Logic • 36-Hour Protocol</p>
      </header>

      <div className="bg-slate-800 p-6 rounded border border-slate-700 max-w-md">
        <h2 className="text-xl font-bold mb-4 flex items-center gap-2">
          <Server className="w-5 h-5 text-blue-400"/>
          Nexus Status
        </h2>
        <div className={`p-4 rounded font-bold text-center ${status.includes('ONLINE') ? 'bg-green-900 text-green-300' : 'bg-red-900 text-red-300'}`}>
          {status}
        </div>
      </div>
    </div>
  )
}

export default App