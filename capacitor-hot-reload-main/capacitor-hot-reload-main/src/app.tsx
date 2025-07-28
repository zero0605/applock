import {useState} from "react";

export default function App() {
  const [count, setCount] = useState(0);
  return <div className={'w-full h-full bg-gray-900 flex flex-col items-center justify-center gap-4'}>
    <div className={'text-white text-2xl font-black'}>
      Hello world
    </div>
    <div className={'text-gray-400 px-8 text-center'}>
      Try editing <span className={'px-2 py-0.5 bg-gray-800 rounded-lg border border-gray-700'}>app.tsx</span> to see hot-reloading works.
    </div>
    <div>
      <button
        className={'bg-gray-800 border border-gray-700 px-4 py-2 text-lg rounded-xl text-white'}
        onClick={() => setCount(count + 1)}
      >
        Tapped {count} times
      </button>
    </div>
  </div>
}
